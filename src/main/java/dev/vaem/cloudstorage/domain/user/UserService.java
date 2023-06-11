package dev.vaem.cloudstorage.domain.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Optional<UserAccount> findByEmail(String username) {
        return userRepository.findByUsername(username).map(userMapper::entityToAccount);
    }

    public Page<User> findAll(int page) {
        return userRepository.findAll(PageRequest.of(page, 20));
    }

    public void addUser(UserCreateRequest userRequest) {
        var user = userMapper.createRequestToEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(String userId, UserUpdateRequest userRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userMapper.updateUser(user, userRequest);
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

}
