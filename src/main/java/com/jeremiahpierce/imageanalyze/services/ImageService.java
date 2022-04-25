package com.jeremiahpierce.imageanalyze.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.entities.ImageMetadata;
import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.exceptions.ImageNotFoundException;
import com.jeremiahpierce.imageanalyze.factory.CloudProviderFactory;
import com.jeremiahpierce.imageanalyze.factory.ObjectDetectionProviderFactory;
import com.jeremiahpierce.imageanalyze.repositories.ImageRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final CloudProviderFactory cloudProviderFactory;
    private final ObjectDetectionProviderFactory objectDetectionProviderFactory;

    @Autowired
    private ModelMapper modelMapper;

    // need to grab the env variable for the cloud provider
    @Value("${heb.cloud.object-detection-provider}")
    private String objectDetectionProvider;

    @Value("${heb.cloud.storage-provider}")
    private String cloudStorageProvider;

    public ImageService(ImageRepository imageRepository, 
                        CloudProviderFactory cloudProviderFactory,
                        ObjectDetectionProviderFactory objectDetectionProviderFactory) {
        this.imageRepository = imageRepository;
        this.cloudProviderFactory = cloudProviderFactory;
        this.objectDetectionProviderFactory = objectDetectionProviderFactory;
    }

    /**
     * 
     * @return all Images from the persistence store
     */
    public List<Images> getAllImages() {
        return imageRepository.findAll();
    }

    /**
     * 
     * @param objects
     * @return all Images by the detected list of objects from the persistence store
     */
    public List<Images> getAllImagesByDetectedObjects(List<String> objects) {
        List<Images> images = imageRepository.findAllByImageMetadataMetadataIn(objects);
        return images;
    }

    public Images getImageById(UUID id) {
        // Add an exception to throw
        return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Could not find the resource " + id));
    }

    public ImageDto sendImage(MultipartFile file, String url, String label, boolean enableObjectDetection) {

        if (file != null) {}
        byte[] imgBytes;
        try {
            imgBytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }

        // Upload the file to the cloud provider
        String imgUrl = cloudProviderFactory
            .getProvider(cloudStorageProvider)
            .upload(label, imgBytes);

        List<ImageMetadata> imageMetadataList = new ArrayList<>();
        Images imageDao = new Images();

        if (enableObjectDetection) {
            Map<String, Float> descriptionAndScore;
                descriptionAndScore = objectDetectionProviderFactory
                    .getObjectDetectionProvider(objectDetectionProvider)
                    .process(imgBytes, label);
            
            if (descriptionAndScore.isEmpty()) {
                for(Map.Entry<String, Float> object : descriptionAndScore.entrySet()) {
                    ImageMetadata imageMetadata = new ImageMetadata(imageDao, object.getKey(), object.getValue());
                    imageMetadataList.add(imageMetadata);
                }   
            }
        }

        imageDao.setLabel(label);
        imageDao.setUrl(imgUrl);

        imageDao.setImageMetadata(imageMetadataList);
        Images savedImage = imageRepository.save(imageDao);
        ImageDto imageDtoMetadataDto = modelMapper.map(savedImage, ImageDto.class);

        return imageDtoMetadataDto;
    }
}
