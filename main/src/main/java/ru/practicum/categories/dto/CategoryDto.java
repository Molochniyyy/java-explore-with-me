package ru.practicum.categories.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.utils.Update;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    Long id;
    @NotNull(groups = {Update.class})
    String name;
}
