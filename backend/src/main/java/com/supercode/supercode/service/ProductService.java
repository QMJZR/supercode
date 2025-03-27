package com.supercode.supercode.service;

import com.supercode.supercode.vo.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductVO> getProducts();

    ProductVO getProductById(String productId) throws Exception;

    MessageVO updateProduct(ProductVO productVO) throws Exception;

    ProductVO createProduct(ProductVO productVO) throws Exception;

    MessageVO deleteProduct(String productId) throws Exception;

}
