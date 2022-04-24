package com.jeremiahpierce.imageanalyze.objectdetection;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.protobuf.ByteString;
import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.entities.ImageMetadata;
import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.factory.CloudProviderFactory;
import com.jeremiahpierce.imageanalyze.interfaces.IObjectAnalysis;
import com.jeremiahpierce.imageanalyze.repositories.ImageRepository;

import org.modelmapper.ModelMapper;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;;

public class GoogleVision implements IObjectAnalysis {


    private CloudProviderFactory cloudProviderFactory;
    private ImageRepository imageRepository;
    private ModelMapper modelMapper;

    public GoogleVision() {}

    public GoogleVision(CloudProviderFactory cloudProviderFactory, ImageRepository imageRepository, ModelMapper modelMapper) {
        this.cloudProviderFactory = cloudProviderFactory;
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Images> getAllImages() {

        return imageRepository.findAll();
    }

    @Override
    public List<Images> getAllImagesByDetectedObjects(List<String> objects) {
        List<Images> images = imageRepository.findAllByImageMetadataMetadataIn(objects);
        return images;
    }

    // private List<EntityAnnotation> getImageLabels(byte[] imageBytes) {
    // return null;

    // }

    @Override
    @Transactional
    public Images getImageById(UUID id) {
        // Add an exception to throw
        return imageRepository.findById(id).orElseThrow(null);
    }

    @Override
    public ImageDto sendImage(byte[] imgBytes, String label, boolean enableObjectDetection) throws IOException {

        // Upload the file to Cloud Storage and get its URL
        String imageUrl = cloudProviderFactory.getProvider("GOOGLE_PROVIDER").upload(label, imgBytes);

        ImageAnnotatorClient vision = ImageAnnotatorClient.create();
        ByteString byteString = ByteString.copyFrom(imgBytes);
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image image = Image.newBuilder().setContent(byteString).build();
        // Maybe make LABEL_DETECTION a env veriable?
        Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
        requests.add(request);
        BatchAnnotateImagesResponse batchResponse = vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = batchResponse.getResponsesList();

        Map<String, Float> annotations = new HashMap<>();
        List<ImageMetadata> imageMetadataList = new ArrayList<>();

        Images imageDao = new Images();
        imageDao.setLabel(label);

        // TODO: create exception
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                ImageMetadata imageMetadata = new ImageMetadata(imageDao, annotation.getDescription(),
                        annotation.getScore());
                imageMetadataList.add(imageMetadata);
                annotations.put(annotation.getDescription(), annotation.getScore());
            }
        }
        imageDao.setImageMetadata(imageMetadataList);
        vision.close();

        Images savedImage = imageRepository.save(imageDao);
        ImageDto imageDtoMetadataDto = modelMapper.map(savedImage, ImageDto.class);
        return imageDtoMetadataDto;
    }
    
}
