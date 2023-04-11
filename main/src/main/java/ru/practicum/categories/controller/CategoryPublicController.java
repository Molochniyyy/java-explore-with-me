package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.utils.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {

    private final CategoryService service;

    @GetMapping
    ResponseEntity<List<CategoryDto>> getAllCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                       Integer fromElement,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size,
                                                       HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        List<CategoryDto> result = service.getCategories(fromElement, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(path = "/{catId}")
    ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId,
                                                HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        CategoryDto result = service.getCategoryById(catId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
