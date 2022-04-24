package com.jeremiahpierce.imageanalyze.dto;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImageDto {

    private UUID id;
    private String label;
    private List<ImageMetadataDto> imageMetadata;
}
