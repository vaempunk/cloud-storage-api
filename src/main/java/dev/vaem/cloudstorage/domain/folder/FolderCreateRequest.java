package dev.vaem.cloudstorage.domain.folder;

import jakarta.validation.constraints.NotBlank;

public record FolderCreateRequest(
        @NotBlank String name) {
}
