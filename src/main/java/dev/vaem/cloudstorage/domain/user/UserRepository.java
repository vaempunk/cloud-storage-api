package dev.vaem.cloudstorage.domain.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    Page<User> findAll(Pageable pageable);
    
}
