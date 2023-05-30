package dev.vaem.cloudstorage.domain.user;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
