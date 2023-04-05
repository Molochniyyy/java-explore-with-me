package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {CategoryService.class})
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "newEventDto.category")
    @Mapping(target = "initiator", source = "initiator")
        // Категория берется с помощью метода в классе CategoryServiceAdminImpl, указанном в аннтотации к этому классу
        // В остальных случаях необходимые сущности передаются вторым, третьим и т.д. аргументами - например, как с User)
    Event fromDto(NewEventDto newEventDto, User initiator);

    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views);
}
