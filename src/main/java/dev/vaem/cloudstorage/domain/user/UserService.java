package dev.vaem.cloudstorage.domain.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.util.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserAccount userAccount() {
        return (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public User getMe() {
        return userRepository.findById(userAccount().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Optional<UserAccount> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::entityToAccount);
    }

    public void addUser(UserRequest userRequest) {
        var user = userMapper.createRequestToEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateMe(UserRequest userRequest) {
        var user = getMe();
        user.setUsername(userRequest.username());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        userRepository.save(user);
    }

}
