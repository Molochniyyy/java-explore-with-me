package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    void deleteCompilation(Long compId);

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest compilationRequest);

    Collection<CompilationDto> findCompilations(boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationById(Integer compId);
}
