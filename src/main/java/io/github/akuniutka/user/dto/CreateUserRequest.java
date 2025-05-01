package io.github.akuniutka.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserRequest(

        @NotBlank
        @Size(max = 50)
        String firstName,

        @NotBlank
        @Size(max = 50)
        String lastName,

        @NotBlank
        @Email
        String email
) {

}
