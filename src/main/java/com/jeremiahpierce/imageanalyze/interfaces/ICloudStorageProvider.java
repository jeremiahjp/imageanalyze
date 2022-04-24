package com.jeremiahpierce.imageanalyze.interfaces;

public interface ICloudStorageProvider {
    

    String upload(String filename, byte[] fileBytes);

    
}
