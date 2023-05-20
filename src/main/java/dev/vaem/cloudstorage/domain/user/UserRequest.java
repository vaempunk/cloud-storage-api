package dev.vaem.cloudstorage.domain.user;

public record UserRequest(
    String username,
    String password
) {
}
