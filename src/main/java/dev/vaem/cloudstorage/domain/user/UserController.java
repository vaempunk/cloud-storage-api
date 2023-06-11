package dev.vaem.cloudstorage.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Page<User> getAllUsers(@PathVariable("page") @Min(0) int page) {
        return userService.findAll(page);
    }

    @PostMapping("/users")
    public void addUser(@RequestBody @Valid UserCreateRequest userRequest) {
        userService.addUser(userRequest);
    }

    @PutMapping("/users/{userId}")
    public void updateUser(@PathVariable("userId") String userId, @RequestBody @Valid UserUpdateRequest userRequest) {
        userService.updateUser(userId, userRequest);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
    }

}
