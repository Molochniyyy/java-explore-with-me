package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class EventMapper {
    public static Event toEvent(NewEventDto dto, User initiator, Category category) {
        return Event.builder()
                .eventDate(dto.getEventDate())
                .paid(dto.isPaid())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .initiator(initiator)
                .category(category)
                .build();
    }

    public static EventShortDto toShortDto(Event event, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests((long) event.getRequests().size())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views == null ? 0 : views)
                .build();
    }

    public static EventFullDto toFullDto(Event event, Long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests((long) event.getRequests().size())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views == null ? 0 : views)
                .build();
    }

    public static Collection<EventShortDto> toShortDto(Collection<Event> events, Map<Long, Long> views) {
        return events.stream().map(event -> EventMapper.toShortDto(event, views.get(event.getId())))
                .collect(Collectors.toList());
    }

    public static Collection<EventFullDto> toFullDto(Collection<Event> events, Map<Long, Long> views) {
        return events.stream().map(event -> EventMapper.toFullDto(event, views.get(event.getId())))
                .collect(Collectors.toList());
    }

    public static EventFullDto toFullDto(Event event, Map<Long, Long> views) {
        return toFullDto(event, views.get(event.getId()));
    }
}
