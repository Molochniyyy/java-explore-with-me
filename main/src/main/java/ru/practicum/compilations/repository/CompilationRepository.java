package ru.practicum.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.compilations.model.Compilation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @NonNull
    @EntityGraph(value = "compilation-entity-graph")
    Optional<Compilation> findById(@NonNull Long id);

    @EntityGraph(value = "compilation-entity-graph")
    List<Compilation> findCompilationsByPinned(boolean pinned, Pageable pageable);
}
