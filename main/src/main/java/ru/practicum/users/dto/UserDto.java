package ru.practicum.users.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.utils.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    Long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Create.class})
    String email;
}
