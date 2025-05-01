package io.github.akuniutka.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDto(

        UUID id,
        String firstName,
        String lastName,
        String email,
        String state,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant registrationDate
) {

}
