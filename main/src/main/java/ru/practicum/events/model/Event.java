package ru.practicum.events.model;

import lombok.Data;
import ru.practicum.categories.model.Category;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 3000)
    String annotation;
    //добавить категорию
    @ManyToOne
    @JoinColumn(nullable = false, name = "category_id")
    Category category;
    LocalDateTime createdOn;
    @Column(nullable = false, length = 10000)
    String description;
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(nullable = false, name = "INITIATOR_ID")
    User initiator;
    @Embedded
    Location location;
    boolean paid;
    @Column(nullable = false)
    Long participantLimit;
    LocalDateTime publishedOn;
    boolean requestModeration;
    @Column(nullable = false, length = 100)
    EventState state;
    @Column(nullable = false, length = 200)
    String title;
}
