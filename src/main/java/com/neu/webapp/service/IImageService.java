package com.neu.webapp.service;

import com.neu.webapp.model.Image;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Ruolin Li
 */
public interface IImageService {

    ResponseEntity<?> saveImage(MultipartFile file, String fileType,Long productId,Long userId);

    List<Image> getImagesByProductId(Long productId);

    ResponseEntity<?> deleteImage(long imageId);
}
