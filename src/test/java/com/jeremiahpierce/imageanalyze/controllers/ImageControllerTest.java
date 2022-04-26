package com.jeremiahpierce.imageanalyze.controllers;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.jeremiahpierce.imageanalyze.dto.ImageDto;
import com.jeremiahpierce.imageanalyze.dto.ImageDtoNoMetadata;
import com.jeremiahpierce.imageanalyze.entities.Images;
import com.jeremiahpierce.imageanalyze.services.ImageService;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

public class ImageControllerTest {

  
    @Test
    void testGetAllImagesWithEmptyObjects_ShouldReturnEmptyListOfImagesDtoAnd200() {

        ImageService imageService = mock(ImageService.class);
        ImageController imageController = new ImageController(imageService);
        List<Images> images = new ArrayList<Images>();
        List<String> objects = new ArrayList<>();
        Mockito.when(imageService.getAllImages()).thenReturn(images);
        ResponseEntity<List<ImageDtoNoMetadata>> responseEntity = imageController.getImages(objects);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(true, responseEntity.getBody().isEmpty());
    }

  
    @Test
    void testGetAllImagesByObjects_ShouldReturnNonEmptyListOfImagesDtoAnd200() {

        ImageService imageService = mock(ImageService.class);
        ImageController imageController = new ImageController(imageService);
        List<String> objects = new ArrayList<>();
        objects.add("Candy");
        objects.add("Else");

        List<Images> images = new ArrayList<Images>();
        Images image = new Images("label", "url");
        images.add(image);

        when(imageService.getAllImagesByDetectedObjects(Mockito.anyList())).thenReturn(images);
        ResponseEntity<List<ImageDtoNoMetadata>> responseEntity = imageController.getImages(objects);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(false, responseEntity.getBody().isEmpty());
    }

    @Test
    void testGetAllImagesWithNullObjects_ShouldReturnEmptyListOfImagesDtoAnd200() {

        ImageService imageService = mock(ImageService.class);
        ImageController imageController = new ImageController(imageService);

        List<Images> images = new ArrayList<Images>();

        when(imageService.getAllImagesByDetectedObjects(Mockito.anyList())).thenReturn(images);
        ResponseEntity<List<ImageDtoNoMetadata>> responseEntity = imageController.getImages(null);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(true, responseEntity.getBody().isEmpty());
    }

    @Test
    void testAnalyzeImageWithUrl_ShouldReturnImageDtoAnd200() {

        ImageService imageService = mock(ImageService.class);
        ImageController imageController = new ImageController(imageService);
        List<Images> images = new ArrayList<Images>();
        MockMultipartFile mockMultipartFile = mock(MockMultipartFile.class);
        
        ImageDto imageDto = new ImageDto();
        imageDto.setLabel("label");
        imageDto.setUrl("mockurl");


        when(imageService.sendImage("url", "label", true)).thenReturn(imageDto);
        ResponseEntity<ImageDto> responseEntity = imageController.analyzeImage("mockurl", "label", false, null);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("label", responseEntity.getBody().getLabel());
        assertEquals("mockurl", responseEntity.getBody().getUrl());
    }


}
