package com.jeremiahpierce.imageanalyze.repositories;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.jeremiahpierce.imageanalyze.entities.Images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Images, UUID> {

    @Query("SELECT DISTINCT i FROM Images i inner join i.imageMetadata imd WHERE imd.description IN (:metadata)")
    List<Images> findAllByImageMetadataMetadataIn(List<String> metadata);
    
}
