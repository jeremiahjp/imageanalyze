package com.jeremiahpierce.imageanalyze.cloudstorage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;

import org.springframework.beans.factory.annotation.Value;

public class GoogleCloudStorage implements ICloudStorageProvider {

    //TODO: these could be env vars or properties
    
    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String BUCKET_NAME = "BUCKET_NAME";
    private final String bucketName;
    private final String projectId;
    // private static final String BUCKET_NAME = "heb-bucket";

    public GoogleCloudStorage() {
        this.bucketName = System.getenv(BUCKET_NAME);
        this.projectId = System.getenv(PROJECT_ID);
    }
    @Override
    public String upload(String label, byte[] fileBytes) {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, label);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, fileBytes);
        return blob.getMediaLink();

    }

}
