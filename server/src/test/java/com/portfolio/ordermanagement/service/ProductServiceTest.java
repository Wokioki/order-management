package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.ProductRequest;
import com.portfolio.ordermanagement.dto.ProductResponse;
import com.portfolio.ordermanagement.entity.Category;
import com.portfolio.ordermanagement.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import com.portfolio.ordermanagement.exception.CategoryNotFoundException;
import com.portfolio.ordermanagement.exception.ProductNotFoundException;
import com.portfolio.ordermanagement.mapper.ProductMapper;
import com.portfolio.ordermanagement.repository.CategoryRepository;
import com.portfolio.ordermanagement.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldCreateProductWhenCategoryExists() {
        ProductRequest request = new ProductRequest(
                "Laptop Pro",
                "Powerful laptop",
                new BigDecimal("1499.99"),
                5,
                "https://example.com/laptop.jpg",
                1L
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setCategory(category);

        ProductResponse expectedResponse = new ProductResponse(
                1L,
                "Laptop Pro",
                "Powerful laptop",
                new BigDecimal("1499.99"),
                5,
                "https://example.com/laptop.jpg",
                1L,
                "Electronics",
                null,
                null
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productMapper.toEntity(request, category))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse result = productService.createProduct(request);

        assertEquals("Laptop Pro", result.name());
        assertEquals(new BigDecimal("1499.99"), result.price());
        assertEquals(1L, result.categoryId());

        verify(productRepository).save(product);
    }

    @Test
    void createProduct_shouldThrowExceptionWhenCategoryDoesNotExist() {
        ProductRequest request = new ProductRequest(
                "Laptop Pro",
                "Powerful laptop",
                new BigDecimal("1499.99"),
                5,
                "https://example.com/laptop.jpg",
                999L
        );

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnProductWhenExists() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setCategory(category);

        ProductResponse expectedResponse = new ProductResponse(
                1L,
                "Laptop Pro",
                "Powerful laptop",
                new BigDecimal("1499.99"),
                5,
                "https://example.com/laptop.jpg",
                1L,
                "Electronics",
                null,
                null
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse result = productService.getProductById(1L);

        assertEquals(1L, result.id());
        assertEquals("Laptop Pro", result.name());
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999L)
        );
    }

    @Test
    void updateProduct_shouldUpdateProductWhenProductAndCategoryExist() {
        ProductRequest request = new ProductRequest(
                "Laptop Pro Updated",
                "Updated laptop",
                new BigDecimal("1599.99"),
                10,
                "https://example.com/laptop-updated.jpg",
                1L
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setCategory(category);

        ProductResponse expectedResponse = new ProductResponse(
                1L,
                "Laptop Pro Updated",
                "Updated laptop",
                new BigDecimal("1599.99"),
                10,
                "https://example.com/laptop-updated.jpg",
                1L,
                "Electronics",
                null,
                null
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse result = productService.updateProduct(1L, request);

        assertEquals("Laptop Pro Updated", result.name());
        assertEquals(new BigDecimal("1599.99"), result.price());

        verify(productMapper).updateEntity(product, request, category);
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_shouldDeleteProductWhenExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void getAllProducts_shouldReturnPagedProducts() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setCategory(category);

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop Pro",
                "Powerful laptop",
                new BigDecimal("1499.99"),
                5,
                "https://example.com/laptop.jpg",
                1L,
                "Electronics",
                null,
                null
        );

        PageRequest pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        when(productMapper.toResponse(product))
                .thenReturn(response);

        var result = productService.getAllProducts(
                "lap",
                1L,
                new BigDecimal("1000"),
                new BigDecimal("2000"),
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop Pro", result.getContent().getFirst().name());
    }
}