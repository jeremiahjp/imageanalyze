package com.jeremiahpierce.imageanalyze.factory;

import com.jeremiahpierce.imageanalyze.interfaces.IObjectAnalysis;
import com.jeremiahpierce.imageanalyze.objectdetection.GoogleVision;

public class ObjectDetectionProviderFactory {

    private static final String ENV_MODE = "";
    private static final String GOOGLE_VISION = "GOOGLE VISION";


    public IObjectAnalysis getObjectDetectionProvider(String mode) {

        switch(mode) {
            case GOOGLE_VISION:
                return new GoogleVision();
            default:
                return new GoogleVision();
        }

    }
    
}
