package ru.practicum.categories.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotNull(groups = {Create.class, Update.class})
    String name;
}
