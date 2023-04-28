package com.neu.webapp.controller;

import com.neu.webapp.model.Image;
import com.neu.webapp.model.Product;
import com.neu.webapp.model.User;
import com.neu.webapp.service.IProductService;
import com.neu.webapp.service.IUserService;
import com.neu.webapp.service.inplement.ImageService;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * @author Ruolin Li
 */
@RestController
@RequestMapping("/v1/product")
@AllArgsConstructor
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService productService;
    @Autowired
    private StatsDClient statsDClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //Get List of All Images Uploaded
    @GetMapping(path ="/{product_id}/image")
    public ResponseEntity<?> getImagesByProductId(@PathVariable(value = "product_id") Long productId,
                                                            @RequestHeader(value = "Authorization") String authorization,
                                                            @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.id.image.get");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"gets image of "+productId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))) {
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        Product product = productService.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        if(product.getOwnerUserId()!=user.getId()){
            return new ResponseEntity<>("{ \"error\": \"Can't retrieve other's image\" }",HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(imageService.getImagesByProductId(productId), HttpStatus.OK);
    }

    @PostMapping(
            path = "/{product_id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("fileType") String fileType,
                                            @PathVariable(value = "product_id") Long productId,
                                            @RequestHeader(value = "Authorization") String authorization,
                                            @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.id.image.post");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"uploads image of "+productId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))) {
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        return imageService.saveImage(file,fileType,productId,user.getId());
    }

    @GetMapping(value = "/{product_id}/image/{image_id}")
    public ResponseEntity<?> getImageDetails(@PathVariable("product_id") Long productId,
                                                 @PathVariable("image_id") Long imageId,
                                                 @RequestHeader(value = "Authorization") String authorization,
                                                 @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.id.image.id.get");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"gets image info, product id:"+productId+",image id:"+imageId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))) {
            //wrong token
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(imageService.getImageDetails(productId,imageId,user.getId()), HttpStatus.OK);
    }
    @DeleteMapping(value = "/{product_id}/image/{image_id}")
    public ResponseEntity<?> deleteImage(@PathVariable("product_id") Long productId,
                                                 @PathVariable("image_id") Long imageId,
                                                 @RequestHeader(value = "Authorization") String authorization,
                                                 @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.id.image.id.delete");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is deleting image, product id:"+productId+",image id:"+imageId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))) {
            //wrong token
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Product product = productService.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        Image image = imageService.getImagebyId(imageId);
        String[] parts = image.getS3BucketPath().split("/");
        String userId = parts[1];
        if(user.getId()!=Integer.parseInt(userId)){
            return new ResponseEntity<>("{ \"error\": \"Can't delete other's image\" }",HttpStatus.FORBIDDEN);
        }
        logger.info("Deleting image id "+imageId);
        return imageService.deleteImage(imageId);
    }
}