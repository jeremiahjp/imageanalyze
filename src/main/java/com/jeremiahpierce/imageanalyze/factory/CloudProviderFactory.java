package com.jeremiahpierce.imageanalyze.factory;

import com.jeremiahpierce.imageanalyze.cloudstorage.GoogleCloudStorage;
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;

import org.springframework.stereotype.Component;

@Component
public class CloudProviderFactory {

    private static final String GOOGLE_CLOUD_STORAGE = "Google Cloud Storage";

    public ICloudStorageProvider getProvider(String provider) {

        switch(provider) {
            case GOOGLE_CLOUD_STORAGE:
                return new GoogleCloudStorage();
            default: 
                return new GoogleCloudStorage();
        }
    }
    
}
