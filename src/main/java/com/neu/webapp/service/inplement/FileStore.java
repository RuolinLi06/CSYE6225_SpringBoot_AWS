package com.neu.webapp.service.inplement;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ruolin Li
 */
@AllArgsConstructor
@Service
public class FileStore {
    @Autowired
    private AmazonS3 amazonS3;

    public void uploadFile(String path,
                       String fileName,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }
    public void deleteFile(String path,String fileName) {
        try {
            amazonS3.deleteObject(path,fileName);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to delete the file", e);
        }
    }


}
