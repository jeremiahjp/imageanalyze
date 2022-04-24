package com.jeremiahpierce.imageanalyze.objectdetection;

import java.util.List;
import java.util.Map;

import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.protobuf.ByteString;
import com.jeremiahpierce.imageanalyze.interfaces.IObjectDetectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

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
@Log4j2
public class GoogleVision implements IObjectDetectionProvider {

    @Value("${heb.cloud.storage-provider}")
    private String cloudStorageProvider;

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

        Map<String, Float> descriptionAndScore = new HashMap<>();

        // TODO: create exception
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                log.error("Error: %s%n", res.getError().getMessage());
                return descriptionAndScore;
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                descriptionAndScore.put(annotation.getDescription(), annotation.getScore());
            }
        }
        vision.close();
        return descriptionAndScore;
    }
    
}
