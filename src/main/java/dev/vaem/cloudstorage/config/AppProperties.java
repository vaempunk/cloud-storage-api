package dev.vaem.cloudstorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("cs")
public class AppProperties {
    private String basePath;
    private String historyPath;
}
