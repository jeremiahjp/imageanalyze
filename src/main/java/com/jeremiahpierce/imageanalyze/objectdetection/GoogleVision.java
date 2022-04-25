package com.jeremiahpierce.imageanalyze.objectdetection;

import java.util.List;
import java.util.Map;

import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.protobuf.ByteString;
import com.jeremiahpierce.imageanalyze.exceptions.ImageAnnotatorClientException;
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

    @Override
    public Map<String, Float> process(byte[] imgBytes) {

        ImageAnnotatorClient vision;
        try {
            vision = ImageAnnotatorClient.create();
        }
        catch (IOException e) {
            log.error("The provider had an error. {}", e.getMessage());
            throw new ImageAnnotatorClientException(String.format("The provider had an error. %s", e.getMessage()));
        }
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
                log.error("Error: {}", res.getError().getMessage(), res.getError());
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                descriptionAndScore.put(annotation.getDescription(), annotation.getScore());
            }
        }
        vision.close();
        return descriptionAndScore;
    }
    
}
