package ru.practicum.compilations.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import ru.practicum.events.model.Event;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@NamedEntityGraph(
        name = "compilation-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "events", subgraph = "events-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "events-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("category"),
                                @NamedAttributeNode("initiator")
                        }
                )
        }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "compilations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    @ToString.Exclude
    List<Event> events;
    @Column(nullable = false, length = 128)
    String title;
    @Column(nullable = false)
    boolean pinned;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Compilation that = (Compilation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
