package io.github.akuniutka.user.dto;

import io.github.akuniutka.validation.NotBlankOrNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUserRequest(

        @NotBlankOrNull
        @Size(max = 50)
        String firstName,

        @NotBlankOrNull
        @Size(max = 50)
        String lastName,

        @NotBlankOrNull
        @Email
        String email,

        State state
) {

    public enum State {
        ACTIVE,
        BLOCKED
    }
}
