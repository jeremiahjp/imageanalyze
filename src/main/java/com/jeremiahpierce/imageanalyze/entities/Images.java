package com.jeremiahpierce.imageanalyze.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter 
@Setter
public class Images {

    @Id
    @GeneratedValue
    private UUID id;
    private String label;
    private String url;
    @OneToMany(mappedBy = "image", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ImageMetadata> imageMetadata = new ArrayList<>();

    public Images() {}

    /**
     * 
     * @param label
     * @param url
     * @param imageMetadata
     */
    public Images(String label, String url, List<ImageMetadata> imageMetadata) {
        this.label = label;
        this.url = url;
        this.imageMetadata = imageMetadata;
    }

    /**
     * 
     * @param label
     * @param url
     */
    public Images(String label, String url) {
        this.label = label;
        this.url = url;
    }
}
