package ru.practicum.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotNull(groups = {Create.class, Update.class})
    String name;
}
