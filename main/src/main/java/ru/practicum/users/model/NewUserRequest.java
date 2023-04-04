package ru.practicum.users.model;

import lombok.Data;

@Data
public class NewUserRequest {
    String email;
    String name;
}
