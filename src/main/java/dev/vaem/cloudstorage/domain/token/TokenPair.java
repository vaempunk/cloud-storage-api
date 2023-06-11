package dev.vaem.cloudstorage.domain.token;

public record TokenPair(
        String accessToken,
        String refreshToken) {
}
