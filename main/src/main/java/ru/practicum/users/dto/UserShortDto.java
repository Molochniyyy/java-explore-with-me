package ru.practicum.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    @NotNull(groups = {Create.class})
    Long id;
    @NotNull(groups = {Create.class})
    String name;
}
