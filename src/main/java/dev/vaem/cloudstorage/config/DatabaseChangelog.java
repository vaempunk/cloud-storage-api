package dev.vaem.cloudstorage.config;

import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.vaem.cloudstorage.domain.user.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "admin-init", order = "1", author = "vaem")
public class DatabaseChangelog {
    
    @Execution
    public void initAdmin(
        MongoTemplate mongoTemplate,
        PasswordEncoder passwordEncoder,
        AppProperties appProperties
    ) {
        var adminUsername = appProperties.getAdminUsername();
        var adminPassword = appProperties.getAdminPassword();
        appProperties.cleanCredentials();
        var user = User.builder()
            .username(adminUsername)
            .roles(Set.of("USER", "ADMIN"))
            .password(passwordEncoder.encode(adminPassword))
            .dateCreated(Instant.now())
            .build();
        System.out.println("password: " + user.getPassword());
        System.out.println("username: " + user.getUsername());
        mongoTemplate.insert(user, "users");
    }

    @RollbackExecution
    public void rollback() {
        
    }

}
