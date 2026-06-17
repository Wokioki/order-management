package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.CategoryRequest;
import com.portfolio.ordermanagement.dto.CategoryResponse;
import com.portfolio.ordermanagement.entity.Category;
import com.portfolio.ordermanagement.exception.CategoryAlreadyExistsException;
import com.portfolio.ordermanagement.exception.CategoryNotFoundException;
import com.portfolio.ordermanagement.mapper.CategoryMapper;
import com.portfolio.ordermanagement.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByNameIgnoreCase(request.name())){
            throw new CategoryAlreadyExistsException(request.name());
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getName().equalsIgnoreCase(request.name())
                && categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new CategoryAlreadyExistsException(request.name());
        }

        categoryMapper.updateEntity(category, request);

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

}
