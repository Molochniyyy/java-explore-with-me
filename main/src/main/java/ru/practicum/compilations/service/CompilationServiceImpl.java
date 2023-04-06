package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.EventServiceImpl;
import ru.practicum.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final CompilationMapper compilationMapper;
    private final EventServiceImpl eventService;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        repository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена"));
        repository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
        Compilation compilation = compilationMapper.toEntity(compilationDto, events);
        repository.save(compilation);
        List<EventShortDto> eventShortDtos = eventService.mapToEventShortDtos(events, false);
        return compilationMapper.toDto(compilation, eventShortDtos);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest compilationRequest, Long compId) {
        Compilation compilation = repository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена или недоступна"));
        if (compilationRequest.getTitle() != null) {
            compilation.setTitle(compilationRequest.getTitle());
        }
        if (compilationRequest.getPinned() != null) {
            compilation.setPinned(compilationRequest.getPinned());
        }
        if (compilationRequest.getEvents() != null) {
            List<Event> allEventsById = eventRepository.findAllByIdIn(compilationRequest.getEvents());
            compilation.setEvents(allEventsById);
        }
        repository.save(compilation);
        List<EventShortDto> eventShortDtos = eventService
                .mapToEventShortDtos(new ArrayList<>(compilation.getEvents()), false);
        return compilationMapper.toDto(compilation, eventShortDtos);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Compilation> compilations = repository.findCompilationsByPinned(pinned, pageable);

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            CompilationDto compilationDto;
            if (compilation.getEvents() == null || compilation.getEvents().size() == 0) {
                compilationDto = compilationMapper.toDto(compilation);
            } else {
                List<EventShortDto> eventShortDtos = eventService.mapToEventShortDtos(
                        new ArrayList<>(compilation.getEvents()), false);
                compilationDto = compilationMapper.toDto(compilation, eventShortDtos);
            }
            result.add(compilationDto);
        }
        return result;
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation compilation = repository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена"));
        List<Event> events = new ArrayList<>(compilation.getEvents());
        List<EventShortDto> eventShortDtos = eventService.mapToEventShortDtos(events, false);
        return compilationMapper.toDto(compilation, eventShortDtos);
    }
}
