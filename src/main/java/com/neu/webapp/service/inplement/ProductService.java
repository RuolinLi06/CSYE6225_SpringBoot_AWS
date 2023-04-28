package com.neu.webapp.service.inplement;

import com.neu.webapp.model.Product;
import com.neu.webapp.repository.ProductRepository;
import com.neu.webapp.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/**
 * @author Ruolin Li
 */
@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Product findProductById(long productId){
        return productRepository.findProductById(productId);
    }

    @Override
    public ResponseEntity<?> getProductById(long productId) {
        Product product = productRepository.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    /*
    - Test various failure scenarios:
        - Missing data for required field.
        - Invalid quantity
            - Negative number
            - String
        - Product SKU should be unique. Adding 2nd product with the same SKU should return an error.
     */
    @Override
    public ResponseEntity<?> createProduct(Product product) {
        try{
            logger.info("Creating product :" + product.getName()+","+product.getSku());
            return new ResponseEntity<>(productRepository.save(product),HttpStatus.CREATED);
        }catch(Exception e){
            logger.warn("Create Product Failed: " + e.getMessage());
            return new ResponseEntity<>("{ \"error\": \"Wrong Parameter\" }",HttpStatus.BAD_REQUEST);
        }
    }
/*
    - Test various failure scenarios:
        - Missing data for required field.
        - Invalid quantity
            - Negative number
            - String
        - Product SKU should be unique. Updating the product's SKU should return an error if another product in the database has same SKU.
 */
    @Override
    public ResponseEntity<?> updateProduct(long productId, Product productDetails) {
        Product product = productRepository.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setManufacturer(productDetails.getManufacturer());
        product.setSku(productDetails.getSku());
        product.setQuantity(productDetails.getQuantity());

        try{
            logger.info("Updating product :" + product.getName()+","+product.getSku());
            return new ResponseEntity<>(productRepository.save(product),HttpStatus.NO_CONTENT);
        }catch(Exception e){
            logger.warn("Update Product Failed: " + e.getMessage());
            return new ResponseEntity<>("{ \"error\": \"Wrong Parameter\" }",HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<?> partialUpdateProduct(long productId, Product productDetails) {

        Product product = productRepository.findProductById(productId);
        if(product==null){
            return new ResponseEntity<>("{ \"error\": \"Product not found\" }",HttpStatus.NOT_FOUND);
        }
        if(productDetails.getName()!=null){
            product.setName(productDetails.getName());
        }
        if(productDetails.getDescription()!=null){
            product.setDescription(productDetails.getDescription());
        }
        if(productDetails.getManufacturer()!=null){
            product.setManufacturer(productDetails.getManufacturer());
        }
        if(productDetails.getSku()!=null){
            product.setSku(productDetails.getSku());
        }
        if(productDetails.getQuantity()!=0){
            product.setQuantity(productDetails.getQuantity());
        }

        try{
            logger.info("Updating product :" + product.getName()+","+product.getSku());
            return new ResponseEntity<>(productRepository.save(product),HttpStatus.NO_CONTENT);
        }catch(Exception e){
            logger.warn("Update Product Failed: " + e.getMessage());
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Authorization check - The product can only be deleted by the user that created it.
    Try deleting a product that has already been deleted.
     */
    @Override
    public ResponseEntity<?> deleteProduct(long productId) {
        Product product = productRepository.findProductById(productId);
        logger.info("Deleting product :" + product.getName()+","+product.getSku());
        productRepository.delete(product);
        return new ResponseEntity<>(product,HttpStatus.NO_CONTENT);
    }

}
