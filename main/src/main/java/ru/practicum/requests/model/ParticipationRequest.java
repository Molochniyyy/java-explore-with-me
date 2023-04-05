package ru.practicum.requests.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "REQUESTS")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    Event event;
    @CreationTimestamp
    LocalDateTime created;
    Long requesterId;
    @Enumerated(EnumType.STRING)
    ParticipationRequestStatus status;

    public ParticipationRequest(User requester, Event event) {
        this.requesterId = requester.getId();
        this.event = event;
        if (event.getRequestModeration()) {
            status = ParticipationRequestStatus.PENDING;
        } else {
            status = ParticipationRequestStatus.CONFIRMED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ParticipationRequest that = (ParticipationRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
