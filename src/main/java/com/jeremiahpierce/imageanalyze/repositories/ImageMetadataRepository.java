package com.jeremiahpierce.imageanalyze.repositories;

import java.util.UUID;

import com.jeremiahpierce.imageanalyze.entities.ImageMetadata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, UUID> {
    
}
