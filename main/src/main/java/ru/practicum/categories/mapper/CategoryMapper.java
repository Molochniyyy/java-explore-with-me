package ru.practicum.categories.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;

@Component
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(NewCategoryDto categoryDto);

    CategoryDto toDto(Category category);

}
