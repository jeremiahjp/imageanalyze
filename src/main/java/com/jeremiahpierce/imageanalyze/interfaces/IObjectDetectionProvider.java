package com.jeremiahpierce.imageanalyze.interfaces;

import java.io.IOException;
import java.util.Map;

public interface IObjectDetectionProvider {

    // /**
    //  * 
    //  * @return JSON response containing all image metadata
    //  */
    // List<Images> getAllImages();

    // /**
    //  * 
    //  * @param objects
    //  * @return
    //  */
    // List<Images> getAllImagesByDetectedObjects(List<String> objects);

    /**
     * 
     * @param imageBytes
     * @param enableObjectDetection Indicator to enable object detection
     * @param label
     * @return JSON response including the image data, it's label, it's identifier provided by the persistent data store
     * and any objects detected (if object detection was enabled)
     */
    Map<String, Float> process(byte[] imageBytes, String label);

    // /**
    //  * 
    //  * @param id
    //  * @return JSON response containing the image metadata for the specified image
    //  */
    // Images getImageById(UUID id);


}
