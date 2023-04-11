package ru.practicum.users.dto;

import lombok.Data;
import ru.practicum.utils.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewUserRequest {
    @NotNull(message = "email can't be null", groups = {Create.class})
    @Email(groups = {Create.class})
    String email;
    @NotNull(message = "name can't be null", groups = {Create.class})
    @NotBlank(groups = {Create.class})
    String name;
}
