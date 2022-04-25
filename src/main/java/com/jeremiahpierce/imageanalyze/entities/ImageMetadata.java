package com.jeremiahpierce.imageanalyze.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "image_metadata")
@Getter
@Setter
public class ImageMetadata {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JsonBackReference
    private Images image;

    private String description;
    private double confidenceScore;

    public ImageMetadata() {}

    public ImageMetadata(Images image, String description, double confidenceScore) {
        this.image = image;
        this.description = description;
        this.confidenceScore = confidenceScore;
    }
}
