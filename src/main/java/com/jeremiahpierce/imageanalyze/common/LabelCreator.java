package com.jeremiahpierce.imageanalyze.common;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class LabelCreator {

    private LabelCreator() {} 

    /**
     * Generates a new random label by splitting the filename by it's extension, generating a random id, and using the parts to create a new label
     * @param filename
     * @return a new generated label
     */
    public static String createNewLabel(String filename) {
        String name = getFilename(filename);
        String[] splitLabel = name.split(".");
        return splitLabel[0] + "-" + NanoIdUtils.randomNanoId() + "." + splitLabel[1];
    }

    public static String getFilename(String filename) {
        if (filename == null) {
            // Should never happen.
            return "";
        }
        // Check for Unix-style path
        int unixSep = filename.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = filename.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = Math.max(winSep, unixSep);
        if (pos != -1)  {
            // Any sort of path separator found...
            return filename.substring(pos + 1);
        }
        else {
            // A plain name
            return filename;
        }
    }    
}
