package com.jeremiahpierce.imageanalyze.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.exceptions.InvalidRequestException;
import com.jeremiahpierce.common.LabelCreator;
import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.services.ImageService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModelMapper modelMapper;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 
     * @return JSON resonse containing all image metadata
     */
    @GetMapping
    public ResponseEntity<List<ImageDto>> getImages(@RequestParam(required = false) List<String> objects) {
        List<Images> listOfImages;
        if (objects == null || objects.isEmpty()) {
            log.info("GET all images");
            listOfImages = imageService.getAllImages();
        }
        else {
            log.info("GET images by detected objects {}", objects);
            listOfImages = imageService.getAllImagesByDetectedObjects(objects);
        }
        List<ImageDto> dtos = listOfImages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(dtos);
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

        ImageDto image;
        if (file == null && (url == null || url.isBlank())) {
            log.info("No file or URL provided");
            throw new InvalidRequestException("No file or URL provided");
        }
        else if (file != null && url != null) {
            log.info("Provided file and URL");
            throw new InvalidRequestException("Provide only a file or a URL.");
        }

        if (label == null || label.isBlank()) {
            label = LabelCreator.createNewLabel(file.getOriginalFilename());
        }

        if (file != null && url == null) {
            image = imageService.sendImage(file, label, enableObjectDetection);
        }
        else {
            image = imageService.sendImage(url, label, enableObjectDetection);
        }
        return ResponseEntity.ok().body(image);
    }

    /**
     * 
     * @param imageId
     * @return
     */
    @GetMapping("{imageId}")
    public ResponseEntity<ImageDto> getImage(@PathVariable UUID imageId) {
        log.info("GET image metadata for imageId", imageId);
        return new ResponseEntity<>(imageService.getImageById(imageId), HttpStatus.OK);
    }

    /**
     * Maps an Images entity to an ImageDto
     * @param image An image to map
     * @return The mapped entity to Dto
     */
    private ImageDto convertToDto(Images image) {
        ImageDto imageDto = modelMapper.map(image, ImageDto.class);
        return imageDto;
    }
}
