package com.lxp.recommend.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.application.port.in.RecommendResultUseCase;
import com.lxp.recommend.application.port.out.ResultRepository;
import com.lxp.recommend.application.port.out.StoragePort;
import com.lxp.recommend.dto.response.EngineCallbackResponse;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendResultService implements RecommendResultUseCase {

    private final StoragePort storagePort;
    private final ResultRepository resultRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @CacheEvict(value = "recommendations", allEntries = true)
    public void processCallback(EngineCallbackResponse callback) {
        if (callback.isCompleted()) {
            processCompletedCallback(callback);
        } else if (callback.isFailed()) {
            processFailedCallback(callback);
        } else {
            log.warn("Unknown callback status: {} for batch: {}", callback.status(), callback.batchId());
        }
    }

    private void processCompletedCallback(EngineCallbackResponse callback) {
        log.info("Processing completed callback for batch: {}, userCount: {}",
                callback.batchId(), callback.userCount());

        try {
            byte[] parquetData = storagePort.downloadAsBytes(callback.resultFilePath());
            List<UserRecommendation> recommendations = parseRecommendationsParquet(parquetData);

            for (UserRecommendation rec : recommendations) {
                String courseIdsJson = objectMapper.writeValueAsString(rec.courseIds());
                resultRepository.upsert(rec.userId(), courseIdsJson, callback.batchId());
            }

            log.info("Successfully processed {} user recommendations for batch: {}",
                    recommendations.size(), callback.batchId());

        } catch (Exception e) {
            log.error("Failed to process callback for batch: {}", callback.batchId(), e);
            throw new BusinessException(ErrorCode.BATCH_JOB_FAILED, e);
        }
    }

    private void processFailedCallback(EngineCallbackResponse callback) {
        log.error("Engine processing failed for batch: {}, errorCode: {}, errorMessage: {}",
                callback.batchId(), callback.errorCode(), callback.errorMessage());
    }

    private List<UserRecommendation> parseRecommendationsParquet(byte[] data) throws IOException {
        Map<String, List<String>> grouped = new LinkedHashMap<>();

        InputFile inputFile = new ByteArrayInputFile(data);

        try (ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(inputFile).build()) {
            GenericRecord record;
            boolean schemaLogged = false;
            while ((record = reader.read()) != null) {
                if (!schemaLogged) {
                    log.info("Result Parquet schema: {}", record.getSchema().getFields().stream()
                            .map(f -> f.name() + ":" + f.schema().getType())
                            .toList());
                    schemaLogged = true;
                }
                String userId = record.get("user_id").toString();
                String courseId = record.get("course_id").toString();
                grouped.computeIfAbsent(userId, k -> new ArrayList<>()).add(courseId);
            }
        }

        return grouped.entrySet().stream()
                .map(e -> new UserRecommendation(e.getKey(), e.getValue()))
                .toList();
    }

    private record UserRecommendation(String userId, List<String> courseIds) {
    }

    private static class ByteArrayInputFile implements InputFile {
        private final byte[] data;

        ByteArrayInputFile(byte[] data) {
            this.data = data;
        }

        @Override
        public long getLength() {
            return data.length;
        }

        @Override
        public SeekableInputStream newStream() {
            return new SeekableByteArrayInputStream(data);
        }
    }

    private static class SeekableByteArrayInputStream extends SeekableInputStream {
        private final byte[] data;
        private final ByteArrayInputStream stream;
        private long pos = 0;

        SeekableByteArrayInputStream(byte[] data) {
            this.data = data;
            this.stream = new ByteArrayInputStream(data);
        }

        @Override
        public long getPos() {
            return pos;
        }

        @Override
        public void seek(long newPos) {
            stream.reset();
            stream.skip(newPos);
            pos = newPos;
        }

        @Override
        public int read() throws IOException {
            int result = stream.read();
            if (result >= 0) pos++;
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int result = stream.read(b, off, len);
            if (result > 0) pos += result;
            return result;
        }

        @Override
        public void readFully(byte[] bytes) throws IOException {
            read(bytes, 0, bytes.length);
        }

        @Override
        public void readFully(byte[] bytes, int start, int len) throws IOException {
            read(bytes, start, len);
        }

        @Override
        public int read(ByteBuffer buf) throws IOException {
            byte[] temp = new byte[buf.remaining()];
            int read = read(temp);
            if (read > 0) buf.put(temp, 0, read);
            return read;
        }

        @Override
        public void readFully(ByteBuffer buf) throws IOException {
            byte[] temp = new byte[buf.remaining()];
            readFully(temp);
            buf.put(temp);
        }
    }
}
