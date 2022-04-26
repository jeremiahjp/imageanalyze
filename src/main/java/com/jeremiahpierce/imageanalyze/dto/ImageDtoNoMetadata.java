package com.jeremiahpierce.imageanalyze.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDtoNoMetadata {
    
    private UUID id;
    private String label;
    private String url;
}
