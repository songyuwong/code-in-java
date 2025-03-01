package com.drizzlepal.springboot.miniostarter;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import com.drizzlepal.springboot.miniostarter.exception.DrizzlepalMinioObjectOpException;
import com.drizzlepal.springboot.miniostarter.props.DrizzlepalMinioStarterProps;
import com.google.common.net.MediaType;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class MinioTemplate {

    private final MinioClient minioClient;

    public MinioTemplate(DrizzlepalMinioStarterProps props) {
        // 自定义 OkHttpClient 连接池
        OkHttpClient customHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES)) // 最大连接数 10，闲置连接存活 5 分钟
                .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间
                .connectTimeout(10, TimeUnit.SECONDS) // 连接超时时间
                .build();
        // 构建 MinioClient
        minioClient = MinioClient.builder()
                .endpoint(props.getEndpoint())
                .credentials(props.getAccessKey(), props.getSecretKey())
                .httpClient(customHttpClient) // 使用自定义 OkHttpClient
                .build();
    }

    public void putImage(String bucketName, String objectName, String filePath)
            throws DrizzlepalMinioObjectOpException {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    .contentType(MediaType.ANY_IMAGE_TYPE.toString()).build());
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException e) {
            throw new DrizzlepalMinioObjectOpException("put image error", e);
        }
    }

    public InputStream getImage(String bucketName, String objectName) throws DrizzlepalMinioObjectOpException {
        try {
            return minioClient
                    .getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException e) {
            throw new DrizzlepalMinioObjectOpException("get image error", e);
        }
    }

    public void makeSureBucketExists(String bucketName) throws DrizzlepalMinioObjectOpException {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException e) {
            throw new DrizzlepalMinioObjectOpException("make sure bucket exists error", e);
        }
    }

}
