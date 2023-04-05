package ru.practicum.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;

import java.util.Collection;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Collection<Compilation> findCompilationsByPinned(boolean pinned, Pageable pageable);
}
