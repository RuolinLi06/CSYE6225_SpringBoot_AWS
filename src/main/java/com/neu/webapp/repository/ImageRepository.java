package com.neu.webapp.repository;

import com.neu.webapp.model.Image;
import com.neu.webapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Ruolin Li
 */
public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findImageByS3BucketPath(String path);
    List<Image> findImagesByProductId(long productId);
    Image findImageByImageId(long imageId);
}
