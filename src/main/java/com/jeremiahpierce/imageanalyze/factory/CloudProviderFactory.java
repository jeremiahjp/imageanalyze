package com.jeremiahpierce.imageanalyze.factory;

import com.jeremiahpierce.imageanalyze.cloudstorage.GoogleCloudStorage;
import com.jeremiahpierce.imageanalyze.cloudstorage.MinioCloudStorage;
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;

import org.springframework.stereotype.Component;

@Component
public class CloudProviderFactory {

    private static final String GOOGLE_CLOUD_STORAGE = "Google Cloud Storage";
    private static final String MINIO_CLOUD_STORAGE = "Minio";

    public ICloudStorageProvider getProvider(String provider) {

        switch(provider) {
            case GOOGLE_CLOUD_STORAGE:
                return new GoogleCloudStorage();
            case MINIO_CLOUD_STORAGE:
                return new MinioCloudStorage();
            default: 
                return new GoogleCloudStorage();
        }
    }
}
