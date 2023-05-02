package ru.practicum.comments.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    User commentator;
    @ManyToOne
    @JoinColumn(name = "id_event", nullable = false)
    Event event;
    @Column(nullable = false, length = 7000)
    String description;
    @Column(nullable = false)
    LocalDateTime created;
}
