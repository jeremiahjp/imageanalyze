package com.jeremiahpierce.imageanalyze.factory;

import com.jeremiahpierce.imageanalyze.interfaces.IObjectDetectionProvider;
import com.jeremiahpierce.imageanalyze.objectdetection.GoogleVision;

import org.springframework.stereotype.Component;

@Component
public class ObjectDetectionProviderFactory {

    private static final String GOOGLE_VISION = "Google Vision";

    public IObjectDetectionProvider getObjectDetectionProvider(String mode) {
        switch(mode) {
            case GOOGLE_VISION:
                return new GoogleVision();
            default:
                return new GoogleVision();
        }

    }
    
}
