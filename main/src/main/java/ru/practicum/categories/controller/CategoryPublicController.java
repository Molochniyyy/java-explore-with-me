package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryServiceImpl;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryServiceImpl service;

    @PostMapping
    ResponseEntity<CategoryDto> saveCategory(@Validated({Create.class}) @RequestBody CategoryDto categoryDto) {
        log.info("\n\nПолучен запрос к эндпоинту: POST /admin/categories" +
                "\nСоздан объект из тела запроса:\n'{}'", categoryDto);
        CategoryDto result = categoryServiceAdmin.saveCategory(categoryDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{catId}")
    ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                               @Validated({Update.class}) @RequestBody CategoryDto categoryDto) {
        log.info("\n\nПолучен запрос к эндпоинту: PATCH /admin/categories/{}\n" +
                "Создан объект из тела запроса:\n'{}'", catId, categoryDto);
        CategoryDto result = categoryServiceAdmin.updateCategory(catId, categoryDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{catId}")
    ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        log.info("\n\nПолучен запрос к эндпоинту: DELETE /admin/categories/{}", catId);
        categoryServiceAdmin.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
