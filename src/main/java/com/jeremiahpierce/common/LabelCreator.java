package com.jeremiahpierce.common;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class LabelCreator {

    public static String createNewLabel(String filename) {
        String[] splitLabel = filename.split("\\.(?=[^\\.]+$)");
        return splitLabel[0] + "-" + NanoIdUtils.randomNanoId() + "." + splitLabel[1];
    }
    
}
