package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.CategoryRequest;
import com.portfolio.ordermanagement.dto.CategoryResponse;
import com.portfolio.ordermanagement.entity.Category;
import com.portfolio.ordermanagement.exception.CategoryAlreadyExistsException;
import com.portfolio.ordermanagement.mapper.CategoryMapper;
import com.portfolio.ordermanagement.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
