package com.neu.webapp.controller;

import com.neu.webapp.model.Product;
import com.neu.webapp.model.User;
import com.neu.webapp.service.IProductService;
import com.neu.webapp.service.IUserService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Ruolin Li
 */
@RestController
@RequestMapping("/v1/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Autowired
    private IUserService userService;
    @Autowired
    private StatsDClient statsDClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //Permissions - Only users with an account can create a product.
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product,
                                                 @RequestHeader(value = "Authorization") String authorization,
                                                 @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.post");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is creating Product");
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))){
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        product.setOwnerUserId(user.getId());
        return productService.createProduct(product);
    }

    @GetMapping(path ="/{id}")
    public ResponseEntity<?> getProductById(@PathVariable(value = "id") Long productId) {
        statsDClient.incrementCounter("endpoint.product.id.get");
        logger.info("Get Product " + productId);
        return productService.getProductById(productId);
    }

    //Only the user who added the product can update the product
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable(value = "id") Long productId, @RequestBody Product productDetails,
                                                 @RequestHeader(value = "Authorization") String authorization,
                                                 @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.id.put");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is updating product "+productId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))){
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        Product product = productService.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        if(product.getOwnerUserId()!= user.getId()){
            //Only the user who added the product can update the product
            return new ResponseEntity<>("{ \"error\": \"Only the user who added the product can update the product\" }",HttpStatus.FORBIDDEN);
        }

        return productService.updateProduct(productId, productDetails);
    }
    //Only the user who added the product can update the product
    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> partialUpdateProduct(@PathVariable(value = "id") Long productId, @RequestBody Product productDetails,
                                                      @RequestHeader(value = "Authorization") String authorization,
                                                      @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.product.id.patch");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is updating product "+productId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))){
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        Product product = productService.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        if(product.getOwnerUserId()!= user.getId()){
            //Only the user who added the product can update the product
            return new ResponseEntity<>("{ \"error\": \"Only the user who added the product can update the product\" }",HttpStatus.FORBIDDEN);
        }
        //System.out.println(productDetails.getQuantity());//quantity default 0 if not in json
        return productService.partialUpdateProduct(productId,productDetails);
    }
    //Only the user who added the product can delete the product
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") Long productId,
                                                 @RequestHeader(value = "Authorization") String authorization,
                                                 @RequestHeader(value = "Token") String token){
        statsDClient.incrementCounter("endpoint.product.id.delete");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is deleting product "+productId);
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))){
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        Product product = productService.findProductById(productId);
        if(product==null){
            //delete unexist product
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        if(product.getOwnerUserId()!= user.getId()){
            //Only the user who added the product can delete the product
            return new ResponseEntity<>("{ \"error\": \"Only the user who added the product can delete the product\" }",HttpStatus.FORBIDDEN);
        }
        return productService.deleteProduct(productId);
    }
}
