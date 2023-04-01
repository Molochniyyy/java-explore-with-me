package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    Integer id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    LocalDateTime eventDate;
    UserShortDto initiator;
    boolean paid;
    String title;
    Integer views;
}
