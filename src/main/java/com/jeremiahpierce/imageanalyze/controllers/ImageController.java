package com.jeremiahpierce.imageanalyze.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.factory.ObjectDetectionProviderFactory;
import com.jeremiahpierce.imageanalyze.interfaces.IObjectAnalysis;
import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.services.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController()
@RequestMapping("/images")
@Slf4j
public class ImageController {

    private final ObjectDetectionProviderFactory objectDetectionProviderFactory;
    private final ImageService imageService;

    private static final String OBJECT_DETECTION_PROVIDER = "GOOGLE VISION";

    public ImageController(ObjectDetectionProviderFactory objectDetectionProviderFactory, ImageService imageService) {
        this.objectDetectionProviderFactory = objectDetectionProviderFactory;
        this.imageService = imageService;
    }

    /**
     * 
     * @return JSON resonse containing all image metadata
     */
    @GetMapping("")
    public ResponseEntity<List<Images>> getImages(@RequestParam(required = false) List<String> objects) {
        log.info("GET images {}", objects);
        // IObjectAnalysis objectAnalysis = objectDetectionProviderFactory
        List<Images> listOfMetadata = imageService.getAllImagesByDetectedObjects(objects);

        return ResponseEntity.ok().body(listOfMetadata);
    }

    /**
     * 
     * @param file
     * @return
     */
    @PostMapping
    public ResponseEntity<ImageDto> analyzeImage(@RequestParam(required = false) String url, @RequestParam(required = false) MultipartFile file) {

        // We have a file uploaded
        ImageDto image = new ImageDto();
        if (file != null) {
            log.info("POST image with FILE: {}", file.getOriginalFilename());
            try {
                image = imageService.sendImage(file.getBytes(), "gummies.jpg", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // TODO: implement url requirement
        else {
            log.info("POST image with url: {}", url);
            return ResponseEntity.ok().body(new ImageDto());
        }
        return ResponseEntity.ok().body(image);
    }

    @GetMapping("{imageId}")
    public ResponseEntity<Images> getImage(@PathVariable UUID imageId) {
        log.info("GET image metadata for imageId", imageId);
        Images image = imageService.getImageById(imageId);
        return ResponseEntity.ok().body(image);

    }

}
