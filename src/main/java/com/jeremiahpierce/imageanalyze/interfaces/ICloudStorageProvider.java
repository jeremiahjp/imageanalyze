package com.jeremiahpierce.imageanalyze.interfaces;

public interface ICloudStorageProvider {
    

    /**
     * 
     * @param filename
     * @param fileBytes
     * @return
     */
    String upload(String filename, byte[] fileBytes);

    
}
