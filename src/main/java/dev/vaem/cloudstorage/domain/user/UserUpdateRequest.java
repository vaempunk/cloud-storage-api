package dev.vaem.cloudstorage.domain.user;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank String username,
        @NotBlank String password,
        @Size(min = 1) List<String> roles) {
}
