package com.jeremiahpierce.imageanalyze.common;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class LabelCreator {

    /**
     * Generates a new random label by splitting the filename by it's extension, generating a random id, and using the parts to create a new label
     * @param filename
     * @return a new generated label
     */
    public static String createNewLabel(String filename) {
        String[] splitLabel = filename.split("\\.(?=[^\\.]+$)");
        return splitLabel[0] + "-" + NanoIdUtils.randomNanoId() + "." + splitLabel[1];
    }
    
}
