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
import com.jeremiahpierce.imageanalyze.interfaces.ICloudStorageProvider;
import com.jeremiahpierce.imageanalyze.interfaces.IObjectDetectionProvider;
import com.jeremiahpierce.imageanalyze.repositories.ImageRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;;

@Component
public class GoogleVision implements IObjectDetectionProvider {


    private CloudProviderFactory cloudProviderFactory;
    // private ImageRepository imageRepository;
    private ModelMapper modelMapper;

    @Value("${heb.cloud.storage-provider}")
    private String cloudStorageProvider;

    public GoogleVision() {}

    public GoogleVision(CloudProviderFactory cloudProviderFactory, ModelMapper modelMapper) {
        this.cloudProviderFactory = cloudProviderFactory;
        // this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    // This should return information about the image from the object detection provider
    @Override
    public Map<String, Float> process(byte[] imgBytes, String label, boolean enableObjectDetection, String imgUrl) throws IOException {

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
        imageDao.setUrl(imgUrl);

        Map<String, Float> descriptionAndScore = new HashMap<>();

        // TODO: create exception
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                ImageMetadata imageMetadata = new ImageMetadata(imageDao, annotation.getDescription(),
                        annotation.getScore());
                // imageMetadataList.add(imageMetadata);
                descriptionAndScore.put(annotation.getDescription(), annotation.getScore());
                annotations.put(annotation.getDescription(), annotation.getScore());
            }
        }
        imageDao.setImageMetadata(imageMetadataList);
        vision.close();

        // this needs to be done in the caller method
        // Images savedImage = imageRepository.save(imageDao);
        // ImageDto imageDtoMetadataDto = modelMapper.map(savedImage, ImageDto.class);
        return descriptionAndScore;
    }
    
}
