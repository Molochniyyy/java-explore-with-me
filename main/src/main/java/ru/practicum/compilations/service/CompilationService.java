package ru.practicum.compilations.service;

import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

import java.util.List;

@Service
public interface CompilationService {
    void deleteCompilation(Long compId);

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest compilationRequest, Long compId);

    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationById(Long compId);
}
