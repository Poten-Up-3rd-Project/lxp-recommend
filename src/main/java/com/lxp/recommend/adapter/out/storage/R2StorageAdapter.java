package com.lxp.recommend.adapter.out.storage;

import com.lxp.recommend.application.port.out.StoragePort;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class R2StorageAdapter implements StoragePort {

    private final S3Client s3Client;

    @Value("${r2.bucket}")
    private String bucket;

    @Override
    public void upload(String key, Path filePath) {
        try {
            ensureBucketExists();
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    RequestBody.fromFile(filePath)
            );
            log.info("Uploaded file to R2: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload file to R2: {}", key, e);
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public void upload(String key, byte[] data) {
        try {
            ensureBucketExists();
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(data)
            );
            log.info("Uploaded bytes to R2: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload bytes to R2: {}", key, e);
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public InputStream download(String key) {
        try {
            return s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to download from R2: {}", key, e);
            throw new BusinessException(ErrorCode.STORAGE_DOWNLOAD_FAILED, e);
        }
    }

    @Override
    public byte[] downloadAsBytes(String key) {
        try (InputStream inputStream = download(key)) {
            return inputStream.readAllBytes();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to download bytes from R2: {}", key, e);
            throw new BusinessException(ErrorCode.STORAGE_DOWNLOAD_FAILED, e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Failed to check existence in R2: {}", key, e);
            return false;
        }
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            log.info("Deleted from R2: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete from R2: {}", key, e);
        }
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("Created bucket: {}", bucket);
        }
    }
}
