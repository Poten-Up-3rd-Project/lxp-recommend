package com.lxp.recommend.adapter;

import com.lxp.recommend.adapter.in.batch.writer.ParquetWriterSupport;
import com.lxp.recommend.adapter.in.batch.writer.ParquetWriterSupport.CourseParquetRecord;
import com.lxp.recommend.adapter.in.batch.writer.ParquetWriterSupport.UserParquetRecord;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
@DisabledOnOs(OS.WINDOWS)
class ParquetWriterSupportTest {

    @Test
    @DisplayName("User Parquet 파일을 생성하고 읽을 수 있다")
    void writeAndReadUsersParquet() throws Exception {
        List<UserParquetRecord> users = List.of(
                new UserParquetRecord(
                        "user-1",
                        List.of(1L, 2L, 3L),
                        1,
                        List.of("course-1", "course-2"),
                        List.of("course-10")
                ),
                new UserParquetRecord(
                        "user-2",
                        List.of(4L, 5L),
                        2,
                        List.of(),
                        List.of()
                )
        );

        java.nio.file.Path parquetFile = ParquetWriterSupport.writeUsersParquet(users);

        assertThat(parquetFile).exists();

        List<GenericRecord> records = readParquetFile(parquetFile);

        assertThat(records).hasSize(2);

        GenericRecord first = records.get(0);
        assertThat(first.get("id").toString()).isEqualTo("user-1");

        List<Long> interestTags = (List<Long>) first.get("interest_tags");
        assertThat(interestTags).containsExactly(1L, 2L, 3L);
        assertThat(first.get("level")).isEqualTo(1);

        List<Object> purchasedCourseIds = (List<Object>) first.get("purchased_course_ids");
        assertThat(purchasedCourseIds)
                .extracting(Object::toString)
                .containsExactly("course-1", "course-2");

        List<Object> createdCourseIds = (List<Object>) first.get("created_course_ids");
        assertThat(createdCourseIds)
                .extracting(Object::toString)
                .containsExactly("course-10");

        GenericRecord second = records.get(1);
        assertThat(second.get("id").toString()).isEqualTo("user-2");
        assertThat(second.get("level")).isEqualTo(2);

        java.nio.file.Files.deleteIfExists(parquetFile);
    }

    @Test
    @DisplayName("Course Parquet 파일을 생성하고 읽을 수 있다")
    void writeAndReadCoursesParquet() throws Exception {
        List<CourseParquetRecord> courses = List.of(
                new CourseParquetRecord("course-1", List.of(1L, 2L), 1),
                new CourseParquetRecord("course-2", List.of(3L, 4L, 5L), 3)
        );

        java.nio.file.Path parquetFile = ParquetWriterSupport.writeCoursesParquet(courses);

        assertThat(parquetFile).exists();

        List<GenericRecord> records = readParquetFile(parquetFile);

        assertThat(records).hasSize(2);

        GenericRecord first = records.get(0);
        assertThat(first.get("id").toString()).isEqualTo("course-1");

        List<Long> tags1 = (List<Long>) first.get("tags");
        assertThat(tags1).containsExactly(1L, 2L);
        assertThat(first.get("level")).isEqualTo(1);

        GenericRecord second = records.get(1);
        assertThat(second.get("id").toString()).isEqualTo("course-2");

        List<Long> tags2 = (List<Long>) second.get("tags");
        assertThat(tags2).containsExactly(3L, 4L, 5L);
        assertThat(second.get("level")).isEqualTo(3);

        java.nio.file.Files.deleteIfExists(parquetFile);
    }

    @Test
    @DisplayName("빈 리스트로 Parquet 파일을 생성할 수 있다")
    void writeEmptyUsersParquet() throws Exception {
        List<UserParquetRecord> users = List.of();

        java.nio.file.Path parquetFile = ParquetWriterSupport.writeUsersParquet(users);

        assertThat(parquetFile).exists();

        List<GenericRecord> records = readParquetFile(parquetFile);
        assertThat(records).isEmpty();

        java.nio.file.Files.deleteIfExists(parquetFile);
    }

    @Test
    @DisplayName("대용량 데이터로 Parquet 파일을 생성할 수 있다")
    void writeLargeUsersParquet() throws Exception {
        List<UserParquetRecord> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            users.add(new UserParquetRecord(
                    "user-" + i,
                    List.of((long) i, (long) (i + 1)),
                    (i % 4) + 1,
                    List.of("course-" + i),
                    List.of()
            ));
        }

        java.nio.file.Path parquetFile = ParquetWriterSupport.writeUsersParquet(users);

        assertThat(parquetFile).exists();

        List<GenericRecord> records = readParquetFile(parquetFile);
        assertThat(records).hasSize(1000);

        java.nio.file.Files.deleteIfExists(parquetFile);
    }

    private List<GenericRecord> readParquetFile(java.nio.file.Path path) throws Exception {
        List<GenericRecord> records = new ArrayList<>();
        Path hadoopPath = new Path(path.toUri());

        try (ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(hadoopPath).build()) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                records.add(record);
            }
        }

        return records;
    }
}
