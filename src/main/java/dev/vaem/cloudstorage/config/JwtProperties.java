package dev.vaem.cloudstorage.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("cs.jwt")
public class JwtProperties {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private Duration accessTokenDuration;
    private Duration refreshTokenDuration;
}
