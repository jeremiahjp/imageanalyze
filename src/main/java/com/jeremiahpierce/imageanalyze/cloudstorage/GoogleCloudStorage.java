package com.jeremiahpierce.imageanalyze.cloudstorage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.base.Preconditions;
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;

public class GoogleCloudStorage implements ICloudStorageProvider {
   
    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String BUCKET_NAME = "BUCKET_NAME";
    private static final String ENV_VAR_MISSING = "Environment variable %s is missing";
    private final String bucketName;
    private final String projectId;

    public GoogleCloudStorage() {
        this.bucketName = System.getenv(BUCKET_NAME);
        this.projectId = System.getenv(PROJECT_ID);
        preconditionsCheck();
    }
    @Override
    public String upload(String label, byte[] fileBytes) {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, label);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, fileBytes);
        return blob.getMediaLink();

    }

    /**
     * Precondition checks for environment variables that may not be null
     */
    private void preconditionsCheck() {
        Preconditions.checkNotNull(bucketName, String.format(ENV_VAR_MISSING, PROJECT_ID));
        Preconditions.checkNotNull(projectId, String.format(ENV_VAR_MISSING, BUCKET_NAME));
    }
}
