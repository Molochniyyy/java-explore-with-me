package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.utils.ControllerLog;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {
    private final CategoryService service;

    @PostMapping
    ResponseEntity<CategoryDto> saveCategory(@Validated({Create.class}) @RequestBody NewCategoryDto categoryDto,
                                             HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        CategoryDto result = service.addCategory(categoryDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{catId}")
    ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                               @Validated({Update.class}) @RequestBody CategoryDto categoryDto,
                                               HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        CategoryDto result = service.updateCategory(catId, categoryDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{catId}")
    ResponseEntity<Void> deleteCategory(@PathVariable Long catId,
                                        HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        service.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
