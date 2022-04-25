package com.jeremiahpierce.imageanalyze.interfaces;

import java.util.Map;

public interface IObjectDetectionProvider {

    /**
     * 
     * @param imageBytes the bytes of the image
     * @param label the label for the object
     * @return
     */
    Map<String, Float> process(byte[] imageBytes);
}
