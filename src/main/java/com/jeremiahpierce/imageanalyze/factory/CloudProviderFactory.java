package com.jeremiahpierce.imageanalyze.factory;

import com.jeremiahpierce.imageanalyze.cloudstorage.GoogleCloudStorage;
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;
import com.jeremiahpierce.imageanalyze.interfaces.IObjectAnalysis;

import org.aspectj.weaver.IClassFileProvider;
import org.springframework.stereotype.Component;

@Component
public class CloudProviderFactory {

    private static final String GOOGLE_CLOUD_STORAGE = "GOOGLE_CLOUD_STORAGE";

    public ICloudStorageProvider getProvider(String provider) {

        switch(provider) {
            case GOOGLE_CLOUD_STORAGE:
                return new GoogleCloudStorage();
            default: 
                return new GoogleCloudStorage();
        }
    }
    
}
