package com.jeremiahpierce.imageanalyze.interfaces;

public interface ICloudStorageProvider {

    /**
     * Uploads a file to the cloud storage provider
     * 
     * @param label The label that will be stored
     * @param fileBytes The bytes of the file
     * @return The URL where the object was stored
     */
    String upload(String label, byte[] fileBytes);
}
