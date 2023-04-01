package ru.practicum.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    Collection<Integer> requestsIds;
    RequestStatus status;
}
