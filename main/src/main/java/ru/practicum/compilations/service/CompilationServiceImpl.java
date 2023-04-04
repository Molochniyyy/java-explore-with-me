package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.exceptions.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;

    @Override
    public void deleteCompilation(Long compId) {
        repository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена"));
        repository.deleteById(compId);
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        return null;
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest compilationRequest) {
        return null;
    }

    @Override
    public Collection<CompilationDto> findCompilations(boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public CompilationDto findCompilationById(Integer compId) {
        return null;
    }
}
