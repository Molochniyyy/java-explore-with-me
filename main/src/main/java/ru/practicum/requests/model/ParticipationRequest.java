package ru.practicum.requests.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    User requester;
    @Enumerated(EnumType.STRING)
    ParticipationRequestStatus status;

    public ParticipationRequest(User requester, Event event) {
        this.requester = requester;
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
