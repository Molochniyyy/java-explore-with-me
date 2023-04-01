package ru.practicum.utils;

import lombok.experimental.UtilityClass;
import net.bytebuddy.dynamic.DynamicType;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@UtilityClass
public class ControllerLog {
    public static String createUrlInfo(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod())
                .append(" ")
                .append(request.getRequestURI());
        Optional.ofNullable(request.getQueryString()).ifPresent(s -> builder.append("?").append(s));
        return builder.toString();
    }
}
