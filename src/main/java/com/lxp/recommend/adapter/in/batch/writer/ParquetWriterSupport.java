package com.lxp.recommend.adapter.in.batch.writer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.OutputFile;
import org.apache.parquet.io.PositionOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ParquetWriterSupport {

    public static final Schema USER_SCHEMA = new Schema.Parser().parse("""
            {
              "type": "record",
              "name": "User",
              "fields": [
                {"name": "id", "type": "string"},
                {"name": "interest_tags", "type": {"type": "array", "items": "long"}},
                {"name": "level", "type": "int"},
                {"name": "purchased_course_ids", "type": {"type": "array", "items": "string"}},
                {"name": "created_course_ids", "type": {"type": "array", "items": "string"}}
              ]
            }
            """);

    public static final Schema COURSE_SCHEMA = new Schema.Parser().parse("""
            {
              "type": "record",
              "name": "Course",
              "fields": [
                {"name": "id", "type": "string"},
                {"name": "tags", "type": {"type": "array", "items": "long"}},
                {"name": "level", "type": "int"}
              ]
            }
            """);

    public static java.nio.file.Path writeUsersParquet(List<UserParquetRecord> users) throws IOException {
        java.nio.file.Path tempFile = Files.createTempFile("users_", ".parquet");
        // Delete the empty file created by createTempFile so ParquetWriter can create it
        Files.deleteIfExists(tempFile);

        OutputFile outputFile = new NioOutputFile(tempFile);

        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(outputFile)
                .withSchema(USER_SCHEMA)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build()) {

            for (UserParquetRecord user : users) {
                GenericRecord record = new GenericData.Record(USER_SCHEMA);
                record.put("id", user.id());
                record.put("interest_tags", user.interestTags());
                record.put("level", user.level());
                record.put("purchased_course_ids", user.purchasedCourseIds());
                record.put("created_course_ids", user.createdCourseIds());
                writer.write(record);
            }
        }

        return tempFile;
    }

    public static java.nio.file.Path writeCoursesParquet(List<CourseParquetRecord> courses) throws IOException {
        java.nio.file.Path tempFile = Files.createTempFile("courses_", ".parquet");
        // Delete the empty file created by createTempFile so ParquetWriter can create it
        Files.deleteIfExists(tempFile);

        OutputFile outputFile = new NioOutputFile(tempFile);

        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(outputFile)
                .withSchema(COURSE_SCHEMA)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build()) {

            for (CourseParquetRecord course : courses) {
                GenericRecord record = new GenericData.Record(COURSE_SCHEMA);
                record.put("id", course.id());
                record.put("tags", course.tags());
                record.put("level", course.level());
                writer.write(record);
            }
        }

        return tempFile;
    }

    public record UserParquetRecord(
            String id,
            List<Long> interestTags,
            int level,
            List<String> purchasedCourseIds,
            List<String> createdCourseIds
    ) {
    }

    public record CourseParquetRecord(
            String id,
            List<Long> tags,
            int level
    ) {
    }

    /**
     * A simple OutputFile implementation that uses Java NIO instead of Hadoop.
     * This avoids the need for winutils.exe on Windows.
     */
    private static class NioOutputFile implements OutputFile {
        private final java.nio.file.Path path;

        NioOutputFile(java.nio.file.Path path) {
            this.path = path;
        }

        @Override
        public PositionOutputStream create(long blockSizeHint) throws IOException {
            OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return new NioPositionOutputStream(out);
        }

        @Override
        public PositionOutputStream createOrOverwrite(long blockSizeHint) throws IOException {
            OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return new NioPositionOutputStream(out);
        }

        @Override
        public boolean supportsBlockSize() {
            return false;
        }

        @Override
        public long defaultBlockSize() {
            return 0;
        }

        @Override
        public String getPath() {
            return path.toString();
        }
    }

    private static class NioPositionOutputStream extends PositionOutputStream {
        private final OutputStream out;
        private long position = 0;

        NioPositionOutputStream(OutputStream out) {
            this.out = out;
        }

        @Override
        public long getPos() {
            return position;
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
            position++;
        }

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
            position += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            position += len;
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }
}
