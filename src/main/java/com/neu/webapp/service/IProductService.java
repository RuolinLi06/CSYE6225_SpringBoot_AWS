package com.neu.webapp.service;

import com.neu.webapp.model.Product;
import com.neu.webapp.model.User;
import org.springframework.http.ResponseEntity;

/**
 * @author Ruolin Li
 */
public interface IProductService {
    ResponseEntity<?> getProductById(long productId);

    ResponseEntity<?> createProduct(Product product);

    ResponseEntity<?> updateProduct(long productId,Product productDetails);

    ResponseEntity<?> partialUpdateProduct(long productId,Product productDetails);

    ResponseEntity<?> deleteProduct(long productId);
    Product findProductById(long productId);
}
