package com.booker.services;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.booker.modules.log.service.LoggerService;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Manages file storage operations using MinIO object storage.
 * Handles avatar uploads, deletions, and public URL generation.
 */
@Service
public class MinioService {

    private final MinioClient minioClient;
    private final LoggerService loggerService;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioService(MinioClient minioClient, LoggerService loggerService) {
        this.minioClient = minioClient;
        this.loggerService = loggerService;
    }

    /**
     * Creates the storage bucket if it doesn't exist yet.
     * Applies a public-read policy so files can be accessed via direct URLs.
     */
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                // bucket with public-read
                String policy = """
                        {
                            "Version": "2012-10-17",
                            "Statement": [
                                {
                                    "Effect": "Allow",
                                    "Principal": {"AWS": "*"},
                                    "Action": ["s3:GetObject"],
                                    "Resource": ["arn:aws:s3:::%s/*"]
                                }
                            ]
                        }
                        """.formatted(bucketName);
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket: " + bucketName, e);
        }
    }

    /**
     * Uploads a file to MinIO and returns its public URL.
     * Files are stored as "avatars/{userId}{extension}".
     * 
     * @param file the uploaded file
     * @param userId user ID used in the filename
     * @return public URL to access the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file, UUID userId) throws IOException {
        ensureBucketExists();

        String extension = getFileExtension(file.getOriginalFilename());
        String objectName = "avatars/" + userId + extension;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Failed to upload file to MinIO", e);
        }

        return buildPublicUrl(objectName);
    }

    /**
     * Uploads a file from an input stream.
     * Alternative upload method when you already have the stream.
     * 
     * @param inputStream file content stream
     * @param size file size in bytes
     * @param contentType MIME type
     * @param userId user ID for filename
     * @param extension file extension (e.g., ".jpg")
     * @return public URL to the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(InputStream inputStream, long size, String contentType, UUID userId, String extension) throws IOException {
        ensureBucketExists();

        String objectName = "avatars/" + userId + extension;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Failed to upload file to MinIO", e);
        }

        return buildPublicUrl(objectName);
    }

    /**
     * Deletes a file from MinIO storage.
     * Doesn't throw errors if file doesn't exist.
     * 
     * @param objectName the object path in MinIO (e.g., "avatars/123.jpg")
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            loggerService.error("Failed to delete file from MinIO: " + objectName + " - " + e.getMessage(), "MinioService");
        }
    }

    /**
     * Extracts the object name from a public URL.
     * Used when you need to delete a file and only have its URL.
     * 
     * @param publicUrl the full public URL
     * @return object name, or null if URL format is invalid
     */
    public String extractObjectName(String publicUrl) {
        if (publicUrl == null || !publicUrl.contains("/" + bucketName + "/")) {
            return null;
        }
        int bucketIndex = publicUrl.indexOf("/" + bucketName + "/");
        return publicUrl.substring(bucketIndex + bucketName.length() + 2);
    }

    /**
     * Builds the public URL for accessing a stored object.
     */
    private String buildPublicUrl(String objectName) {
        return endpoint + "/" + bucketName + "/" + objectName;
    }

    /**
     * Extracts file extension from filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
