package ru.practicum.events.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import ru.practicum.categories.model.Category;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NamedEntityGraph(
        name = "event-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("category"),
                @NamedAttributeNode("initiator"),
                @NamedAttributeNode("requests"),
        }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 3000)
    String annotation;
    @ManyToOne
    @JoinColumn(nullable = false, name = "category_id")
    Category category;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    List<ParticipationRequest> requests;
    LocalDateTime createdOn;
    @Column(nullable = false, length = 10000)
    String description;
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(nullable = false, name = "initiator_id")
    User initiator;
    @Embedded
    Location location;
    Boolean paid;
    @Column(nullable = false)
    Long participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    EventState state;
    @Column(nullable = false, length = 200)
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
