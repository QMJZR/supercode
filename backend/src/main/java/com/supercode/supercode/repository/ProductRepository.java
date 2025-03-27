package com.supercode.supercode.repository;

import com.supercode.supercode.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByProductId(Integer productId);

}
