package ru.practicum.compilations.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Event;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "compilations")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToMany
    @JoinTable(name = "COMPILATION_EVENT",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    Collection<Event> events;
    @Column(nullable = false, length = 128)
    String title;
    @Column(nullable = false)
    boolean pinned;
}
