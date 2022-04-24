package com.jeremiahpierce.imageanalyze.cloudstorage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;

public class GoogleCloudStorage implements ICloudStorageProvider {

    //TODO: these could be env vars or properties
    private static final String PROJECT_ID = "heb-code-exercise";
    private static final String BUCKET_NAME = "heb-bucket";

    @Override
    public String upload(String label, byte[] fileBytes) {

        Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
        BlobId blobId = BlobId.of(BUCKET_NAME, label);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, fileBytes);
        return blob.getMediaLink();

    }

}
