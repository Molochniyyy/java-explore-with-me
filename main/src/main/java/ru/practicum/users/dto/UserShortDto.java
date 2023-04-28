package ru.practicum.users.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    @NotNull(groups = {Create.class})
    Long id;
    @NotNull(groups = {Create.class})
    String name;
}
