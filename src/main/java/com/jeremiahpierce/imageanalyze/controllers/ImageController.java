package com.jeremiahpierce.imageanalyze.controllers;

import java.util.List;
import java.util.UUID;

import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.exceptions.InvalidRequestException;
import com.jeremiahpierce.imageanalyze.common.LabelCreator;
import com.jeremiahpierce.imageanalyze.common.MapperUtil;
import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.dto.ImageDtoNoMetadata;
import com.jeremiahpierce.imageanalyze.services.ImageService;

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
     * @param objects A list of objects to be searched on
     * @return  Returns a JSON response body containing only images that have 
                the detected objects specified in the query parameter.
     */
    @GetMapping(value = "", params = "objects")
    public ResponseEntity<List<ImageDtoNoMetadata>> getImages(@RequestParam(required = true) List<String> objects) {
        List<Images> listOfImages;
        log.info("GET images by detected objects {}", objects);
        listOfImages = imageService.getAllImagesByDetectedObjects(objects);
        List<ImageDtoNoMetadata> imageDtoNoMetadata = MapperUtil.mapList(listOfImages, ImageDtoNoMetadata.class);
        return ResponseEntity.ok().body(imageDtoNoMetadata);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<ImageDtoNoMetadata>> getImages() {
        log.info("GET all images");
        List<Images> listOfImages;
        listOfImages = imageService.getAllImages();        
        List<ImageDtoNoMetadata> imageDtoNoMetadata = MapperUtil.mapList(listOfImages, ImageDtoNoMetadata.class);

        return ResponseEntity.ok().body(imageDtoNoMetadata);
    }

    /**
     * 
     * @param url An optional url to an image
     * @param label An optional label for the image
     * @param enableObjectDetection Indicator to do object detection on the image
     * @param file The uploaded file
     * @return Returns a JSON response body including the image data, its label 
                (generate one if the user did not provide it), its identifier provided by the persistent data 
                store, and any objects detected (if object detection was enabled)
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

        if (file != null && label == null || label.isBlank()) {
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
     * @param imageId The id of the image
     * @return  Returns a JSON response containing image metadata for the 
                specified image
     */
    @GetMapping("{imageId}")
    public ResponseEntity<ImageDto> getImage(@PathVariable UUID imageId) {
        log.info("GET image metadata for imageId", imageId);
        return new ResponseEntity<>(imageService.getImageById(imageId), HttpStatus.OK);
    }
}
