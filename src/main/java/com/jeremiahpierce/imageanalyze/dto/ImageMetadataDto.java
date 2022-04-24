package com.jeremiahpierce.imageanalyze.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageMetadataDto {

    private String description;
    private double confidenceScore;
}
