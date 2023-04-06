package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toEntity(newCategoryDto);
        try {
            categoryRepository.save(category);
            return categoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Имя категории должно быть уникальным");
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена"));
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория не найдена или недоступна"));
        try {
            if (categoryDto.getName() != null) {
                category.setName(categoryDto.getName());
            }
            categoryRepository.save(category);
            return categoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Имя категории должно быть уникальным");
        }
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).toList();
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория не найдена или недоступна"));
        return categoryMapper.toDto(category);
    }
}
