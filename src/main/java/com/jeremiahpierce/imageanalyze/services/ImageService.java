package com.jeremiahpierce.imageanalyze.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.entities.ImageMetadata;
import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.exceptions.ImageNotFoundException;
import com.jeremiahpierce.imageanalyze.exceptions.ReadingFileBytesException;
import com.jeremiahpierce.imageanalyze.factory.CloudProviderFactory;
import com.jeremiahpierce.imageanalyze.factory.ObjectDetectionProviderFactory;
import com.jeremiahpierce.imageanalyze.repositories.ImageRepository;

import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final CloudProviderFactory cloudProviderFactory;
    private final ObjectDetectionProviderFactory objectDetectionProviderFactory;

    @Autowired
    private ModelMapper modelMapper;

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
        return imageRepository.findAllByImageMetadataMetadataIn(objects);
    }

    /**
     * 
     * @param id
     * @return
     */
    public ImageDto getImageById(UUID id) {
        Images image = imageRepository.findById(id)
            .orElseThrow(() -> new ImageNotFoundException("Could not find the resource " + id));
        return modelMapper.map(image, ImageDto.class);
    }

    /**
     * Grabs the file bytes and sends the image off to be analyzed, depending if that feature is enabled
     * @param file A file to be downloaded
     * @param label A label for the image
     * @param enableObjectDetection True is object detection enabled, false is not.
     * @return The saved Images converted into a Dto from the persistence provider 
     */
    public ImageDto sendImage(MultipartFile file, String label, boolean enableObjectDetection) {

        byte[] imgBytes = null;
        try {
            if (!file.isEmpty()) {
                imgBytes = file.getBytes();
            }
        } catch (IOException e) {
            throw new ReadingFileBytesException(String.format("We had an error reading the uploaded file. %s", e.getMessage()));
        }

        String imgUrl = cloudProviderFactory
                .getProvider(cloudStorageProvider)
                .upload(label, imgBytes);

        Images imageDao = new Images(label, imgUrl);
        if (enableObjectDetection) {
            Map<String, Float> descriptionAndScore = processObjectDetection(imgBytes);
            setImageMetadata(imageDao, descriptionAndScore);
        }
        Images savedImage = imageRepository.save(imageDao);
        return modelMapper.map(savedImage, ImageDto.class);
    }

    /**
     * Grabs the url bytes and sends the image off to be analyzed, depending if that feature is enabled
     * 
     * @param url A url to be downloaded
     * @param label A label for the image
     * @param enableObjectDetection True is object detection enabled, false is not.
     * @return The saved Images converted to a Dto from the persistence provider
     */
    public ImageDto sendImage(String url, String label, boolean enableObjectDetection) {
        byte[] imgBytes;
        try {
            URL imageUrl = new URL(url);
            InputStream in = imageUrl.openStream();
            imgBytes = IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new ReadingFileBytesException(String.format("We had an error getting the file at the url %s. %s", url, e.getMessage()));
        }
        String imgUrl = uploadToCloudProvider(label, imgBytes);

        Images imageDao = new Images(label, imgUrl);
        if (enableObjectDetection) {
            Map<String, Float> descriptionAndScore = processObjectDetection(imgBytes);
            setImageMetadata(imageDao, descriptionAndScore);
        }
        Images savedImage = imageRepository.save(imageDao);
        return modelMapper.map(savedImage, ImageDto.class);
    }

    /**
     * Takes some bytes from an image and returns the detected objects.
     * 
     * @param imgBytes The bytes from the image
     * @return A map of detected objects
     */
    private Map<String, Float> processObjectDetection(byte[] imgBytes) {
        return objectDetectionProviderFactory
                .getObjectDetectionProvider(objectDetectionProvider)
                .process(imgBytes);
    }

    /**
     * Sets the imageMetadata field for the provided Images object from the provided Map of descriptions and scores
     * 
     * @param image Sets the image metadata fields for the provided Images
     * @param descriptionAndScore A map of description key and score values
     * @return The Images with the set list of image metadata
     */
    private Images setImageMetadata(Images image, Map<String, Float> descriptionAndScore) {
        List<ImageMetadata> imageMetadataList = new ArrayList<>();
        for (Map.Entry<String, Float> object : descriptionAndScore.entrySet()) {
            ImageMetadata imageMetadata = new ImageMetadata(image, object.getKey(), object.getValue());
            imageMetadataList.add(imageMetadata);
        }
        image.setImageMetadata(imageMetadataList);
        return image;
    }

    /**
     * Uploads the bytes to the cloud provider with the provided label
     * 
     * @param label
     * @param imgBytes
     * @return url to the uploaded object
     */
    private String uploadToCloudProvider(String label, byte[] imgBytes) {
        return cloudProviderFactory
                .getProvider(cloudStorageProvider)
                .upload(label, imgBytes);
    }
}
