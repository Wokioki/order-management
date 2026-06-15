package com.portfolio.ordermanagement.mapper;

import com.portfolio.ordermanagement.dto.CategoryRequest;
import com.portfolio.ordermanagement.dto.CategoryResponse;
import com.portfolio.ordermanagement.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request){
        Category category = new Category();

        category.setName(request.name());
        category.setDescription(request.description());

        return category;
    }

    public CategoryResponse toResponse(Category category){
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public void updateEntity(Category category, CategoryRequest request){
        category.setName(request.name());
        category.setDescription(request.description());
    }

}
