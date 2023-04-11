package ru.practicum.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.ParticipationRequestStatus;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    Long id;
    @NotNull(groups = {Create.class, Update.class})
    Long event;
    LocalDateTime created;
    @NotNull(groups = {Create.class, Update.class})
    Long requester;
    ParticipationRequestStatus status;
}
