package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.ProductRequest;
import com.portfolio.ordermanagement.dto.ProductResponse;
import com.portfolio.ordermanagement.entity.Category;
import com.portfolio.ordermanagement.entity.Product;
import com.portfolio.ordermanagement.exception.CategoryNotFoundException;
import com.portfolio.ordermanagement.mapper.ProductMapper;
import com.portfolio.ordermanagement.repository.CategoryRepository;
import com.portfolio.ordermanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Product product = productMapper.toEntity(request, category);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

}
