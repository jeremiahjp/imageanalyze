package com.jeremiahpierce.imageanalyze.services;

import java.util.List;

import com.jeremiahpierce.imageanalyze.entities.ImageMetadata;
import com.jeremiahpierce.imageanalyze.repositories.ImageMetadataRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageMetadataService {

    private final ImageMetadataRepository imageMetadataRepository;

    public List<ImageMetadata> getAllImageMetadata() {
        return imageMetadataRepository.findAll();
    }

}
