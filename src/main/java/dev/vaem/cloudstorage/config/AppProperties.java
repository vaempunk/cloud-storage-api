package dev.vaem.cloudstorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("cs")
public class AppProperties {
    private String basePath;
    private String historyPath;

    @Value("${cs.admin.username}")
    private String adminUsername;
    @Value("${cs.admin.password}")
    private String adminPassword;

    public void cleanCredentials() {
        this.adminUsername = null;
        this.adminPassword = null;
    }
}
