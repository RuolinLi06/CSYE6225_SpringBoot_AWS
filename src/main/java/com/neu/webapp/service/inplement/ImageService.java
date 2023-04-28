package com.neu.webapp.service.inplement;


import com.amazonaws.AmazonServiceException;
import com.neu.webapp.model.Image;
import com.neu.webapp.repository.ImageRepository;
import com.neu.webapp.service.IImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import static org.apache.http.entity.ContentType.*;

/**
 * @author Ruolin Li
 */
@Service
//@Profile("aws")
public class ImageService implements IImageService {
    @Autowired
    private FileStore fileStore;
    @Autowired
    private ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Override
    public ResponseEntity<?> saveImage(MultipartFile file, String fileType, Long productId, Long userId) {
        //check if the file is empty
        if (file.isEmpty()) {
            return new ResponseEntity<>("{ \"error\": \"Cannot upload empty file\" }",HttpStatus.BAD_REQUEST);

        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            return new ResponseEntity<>("{ \"error\": \"FIle uploaded is not an image\" }",HttpStatus.BAD_REQUEST);

        }
        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        //Save Image file in S3 and then save Image in the database
        Long fileId = generateRandomId();
        String path = String.format("%s/%s/%s", bucketName, userId.toString(),fileId.toString());
        String fileName = String.format("%s", file.getOriginalFilename());
        try {
            fileStore.uploadFile(path, fileName, Optional.of(metadata), file.getInputStream());
        } catch (IOException e) {
            logger.warn("Upload Image Failed: "+e.getMessage());
            return new ResponseEntity<>("{ \"error\": \"Failed to upload file\" }",HttpStatus.BAD_REQUEST);
        }
        logger.info("Uploading image for "+productId);
        Image image = Image.builder()
                .productId(productId)
                .s3BucketPath(path)
                .fileName(fileName)
                .build();
        imageRepository.save(image);
        return new ResponseEntity<>(imageRepository.findImageByS3BucketPath(path),HttpStatus.CREATED);
    }

    @Override
    public List<Image> getImagesByProductId(Long productId) {
        List<Image> images = new ArrayList<>();
        imageRepository.findImagesByProductId(productId).forEach(images::add);
        return images;
    }
    private long generateRandomId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public Image getImageDetails(Long productId, Long imageId, Long userId){
        return imageRepository.findImageByImageId(imageId);
    }

    @Override
    public ResponseEntity<?> deleteImage(long imageId) {
        Image image = imageRepository.findImageByImageId(imageId);

        try {
            fileStore.deleteFile(image.getS3BucketPath(),image.getFileName());
        } catch (AmazonServiceException e) {
            logger.warn("Delete Image Failed: "+e.getErrorMessage());
            return new ResponseEntity<>("{ \"error\": \"Failed to delete file\" }",HttpStatus.BAD_REQUEST);

        }
        imageRepository.delete(image);
        return new ResponseEntity<>(image, HttpStatus.NO_CONTENT);
    }
    public Image getImagebyId(long imageId){
        return imageRepository.findImageByImageId(imageId);
    }
}