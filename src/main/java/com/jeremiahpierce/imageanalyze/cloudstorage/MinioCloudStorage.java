package com.jeremiahpierce.imageanalyze.cloudstorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;

import org.springframework.stereotype.Service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

/**
 * A Minio based repository implementation
 */
@Service
public class MinioCloudStorage implements ICloudStorageProvider {

    private final String minioSecretKey = System.getenv("MINIO_SECRET_KEY");
    private final String minioAccessKey = System.getenv("MINIO_ACCESS_KEY");
    // private final String s3BucketUuid;
    private final String minioBucketName = "heb-bucket";
    private final String awsDefaultRegion = System.getenv("");
    private final String minioPort = System.getenv("MINIO_PORT");
    private final String minioHost = System.getenv("MINIO_HOST");
    private int partSize = 10485760;


    @Override
    public String upload(String filename, byte[] fileBytes) {

        InputStream file = new ByteArrayInputStream(fileBytes);
        Optional<String> optionalResponse = Optional.empty();
        try {
            MinioClient minioClient = MinioClient.builder()
                .endpoint("https://s3.amazonaws.com")
                .credentials(minioAccessKey, minioSecretKey)
                .build();

            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());
            }
            ObjectWriteResponse resp = minioClient.putObject(PutObjectArgs.builder().bucket(minioBucketName)
                    .object(filename)
                    .stream(file, fileBytes.length, partSize).build());
            return String.format("https://%s.s3.amazonaws.com/%s", minioBucketName,filename);
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException e) {
            optionalResponse = Optional
                    .of(String.format("There was an error processing your request, %s", e.getMessage()));
                return optionalResponse.get();
        }
    }
}

