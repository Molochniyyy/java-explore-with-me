package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.ParticipationRequestStatus;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    Collection<Long> requestsIds;
    ParticipationRequestStatus status;
}
