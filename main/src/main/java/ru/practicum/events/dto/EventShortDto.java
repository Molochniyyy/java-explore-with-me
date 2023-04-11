package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    Long id;
    @NotNull(groups = {Create.class})
    String annotation;
    @NotNull(groups = {Create.class})
    CategoryDto category;
    Long confirmedRequests;
    @NotNull(groups = {Create.class})
    LocalDateTime eventDate;
    @NotNull(groups = {Create.class})
    UserShortDto initiator;
    @NotNull(groups = {Create.class})
    boolean paid;
    @NotNull(groups = {Create.class})
    String title;
    Long views;
}
