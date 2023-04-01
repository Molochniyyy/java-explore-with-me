package ru.practicum.users.model;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Email(message = "email должен быть корректным")
    String email;
}
