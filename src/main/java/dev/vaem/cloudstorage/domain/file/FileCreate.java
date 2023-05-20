package dev.vaem.cloudstorage.domain.file;

import org.springframework.web.multipart.MultipartFile;

public record FileCreate(
        String name,
        MultipartFile file) {
}
