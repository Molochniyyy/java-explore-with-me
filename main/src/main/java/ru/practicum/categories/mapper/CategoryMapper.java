package ru.practicum.categories.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryDto categoryDto);

    Category toEntity(NewCategoryDto categoryDto);

    CategoryDto toDto(Category category);

}
