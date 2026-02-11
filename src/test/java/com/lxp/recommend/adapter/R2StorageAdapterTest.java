package com.lxp.recommend.adapter;

import com.lxp.recommend.adapter.out.storage.R2StorageAdapter;
import com.lxp.recommend.global.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
class R2StorageAdapterTest {

    @Container
    static MinIOContainer minioContainer = new MinIOContainer("minio/minio:latest")
            .withUserName("minioadmin")
            .withPassword("minioadmin");

    private R2StorageAdapter r2StorageAdapter;
    private S3Client s3Client;
    private static final String BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(minioContainer.getS3URL()))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("minioadmin", "minioadmin")
                ))
                .forcePathStyle(true)
                .build();

        r2StorageAdapter = new R2StorageAdapter(s3Client);
        ReflectionTestUtils.setField(r2StorageAdapter, "bucket", BUCKET);
    }

    @AfterEach
    void tearDown() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    @Test
    @DisplayName("바이트 배열을 업로드하고 다운로드할 수 있다")
    void uploadAndDownloadBytes() {
        String key = "test/data.txt";
        byte[] content = "Hello, MinIO!".getBytes(StandardCharsets.UTF_8);

        r2StorageAdapter.upload(key, content);

        byte[] downloaded = r2StorageAdapter.downloadAsBytes(key);

        assertThat(downloaded).isEqualTo(content);
    }

    @Test
    @DisplayName("파일을 업로드하고 다운로드할 수 있다")
    void uploadAndDownloadFile() throws Exception {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "Hello from file!");

        String key = "test/file.txt";
        r2StorageAdapter.upload(key, tempFile);

        byte[] downloaded = r2StorageAdapter.downloadAsBytes(key);

        assertThat(new String(downloaded, StandardCharsets.UTF_8)).isEqualTo("Hello from file!");

        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("파일 존재 여부를 확인할 수 있다")
    void exists() {
        String key = "test/exists.txt";
        byte[] content = "test".getBytes();

        assertThat(r2StorageAdapter.exists(key)).isFalse();

        r2StorageAdapter.upload(key, content);

        assertThat(r2StorageAdapter.exists(key)).isTrue();
    }

    @Test
    @DisplayName("파일을 삭제할 수 있다")
    void delete() {
        String key = "test/delete.txt";
        byte[] content = "to be deleted".getBytes();

        r2StorageAdapter.upload(key, content);
        assertThat(r2StorageAdapter.exists(key)).isTrue();

        r2StorageAdapter.delete(key);

        assertThat(r2StorageAdapter.exists(key)).isFalse();
    }

    @Test
    @DisplayName("InputStream으로 다운로드할 수 있다")
    void downloadAsInputStream() throws Exception {
        String key = "test/stream.txt";
        String content = "Stream content";
        r2StorageAdapter.upload(key, content.getBytes());

        try (InputStream is = r2StorageAdapter.download(key)) {
            String downloaded = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(downloaded).isEqualTo(content);
        }
    }

    @Test
    @DisplayName("존재하지 않는 파일 다운로드 시 예외가 발생한다")
    void downloadNonExistent() {
        String key = "test/nonexistent.txt";

        assertThatThrownBy(() -> r2StorageAdapter.downloadAsBytes(key))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("대용량 파일을 업로드하고 다운로드할 수 있다")
    void uploadLargeFile() {
        String key = "test/large.bin";
        byte[] largeContent = new byte[1024 * 1024];
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        r2StorageAdapter.upload(key, largeContent);
        byte[] downloaded = r2StorageAdapter.downloadAsBytes(key);

        assertThat(downloaded).isEqualTo(largeContent);
    }

    @Test
    @DisplayName("중첩된 경로에 파일을 업로드할 수 있다")
    void uploadNestedPath() {
        String key = "exports/2026/02/07/batch_20260207_0600/users.parquet";
        byte[] content = "nested content".getBytes();

        r2StorageAdapter.upload(key, content);

        assertThat(r2StorageAdapter.exists(key)).isTrue();
        assertThat(r2StorageAdapter.downloadAsBytes(key)).isEqualTo(content);
    }
}
