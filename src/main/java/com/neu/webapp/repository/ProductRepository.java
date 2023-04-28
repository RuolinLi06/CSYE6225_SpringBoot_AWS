package com.neu.webapp.repository;

import com.neu.webapp.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Ruolin Li
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductById(long id);

    /*
    "dateAdded": "2023-02-06T05:54:29.536+00:00",
    "dataLastUpdated": "2023-02-06T05:54:29.536+00:00",
    dataLastUpdated don't change when partial update
     */
    @Modifying
    @Transactional
    @Query("update Product p set " +
            "p.name = CASE WHEN :#{#updateP.name} IS NULL THEN p.name ELSE :#{#updateP.name} END ," +
            "p.description = CASE WHEN :#{#updateP.description} IS NULL THEN p.description ELSE :#{#updateP.description} END ," +
            "p.sku = CASE WHEN :#{#updateP.sku} IS NULL THEN p.sku ELSE :#{#updateP.sku} END ," +
            "p.manufacturer =  CASE WHEN :#{#updateP.manufacturer} IS NULL THEN p.manufacturer ELSE :#{#updateP.manufacturer} END ," +
            "p.quantity = CASE WHEN :#{#updateP.quantity} = 0 THEN p.quantity ELSE :#{#updateP.quantity} END " +
            "where p.id = :#{#id}")
    int partialUpdateProduct(long id,Product updateP);

}
