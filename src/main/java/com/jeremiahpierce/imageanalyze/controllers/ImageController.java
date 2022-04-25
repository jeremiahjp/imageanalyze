package com.jeremiahpierce.imageanalyze.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.exceptions.InvalidRequestException;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.jeremiahpierce.common.LabelCreator;
import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.services.ImageService;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/images")
@Slf4j
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
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
    public ResponseEntity<ImageDto> analyzeImage(@RequestParam(required = false) String url,
            @RequestParam(required = false) String label,
            @RequestParam boolean enableObjectDetection,
            @RequestParam(required = false) MultipartFile file) {


        // if (file == null && url == null) {
        //     log.info("No file or URL provided");
        //     throw new InvalidRequestException("No file or URL provided");
        // }

        ImageDto image = new ImageDto();
        // byte[] fileBytes = null;

        // if (label == null || label.isBlank()) {
        //     if (url != null) {
        //         label = LabelCreator.createNewLabel(url);
        //     }
        //     else if (file != null) {
        //         label = LabelCreator.createNewLabel(file.getOriginalFilename());
        //     }
        // }

        // if (file != null) {
        //     log.info("POST image with FILE: {}", file.getOriginalFilename());
        //     try {
        //         fileBytes = file.getBytes();
        //     } catch (IOException e) {
        //         log.error("Error reading the file: {}", e.getMessage());
        //         e.printStackTrace();
        //         return null;
        //     }
        // } 
        // else {
        //     log.info("POST image with url: {}", url);
        //     try {
        //         URL imageUrl = new URL(url);
        //         InputStream in = imageUrl.openStream();
        //         fileBytes = IOUtils.toByteArray(in);
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //         return null;
        //     }
        // }

        // try {
        image = imageService.sendImage(file, url, label, enableObjectDetection);
        // } catch (IOException e) {
        //     log.error("Error reading the file: {}", e.getMessage());
        //     return null;
        // }
        return ResponseEntity.ok().body(image);
    }

    /**
     * 
     * @param imageId
     * @return
     */
    @GetMapping("{imageId}")
    public ResponseEntity<Images> getImage(@PathVariable UUID imageId) {
        log.info("GET image metadata for imageId", imageId);
        return new ResponseEntity<>(imageService.getImageById(imageId), HttpStatus.OK);
    }
}
