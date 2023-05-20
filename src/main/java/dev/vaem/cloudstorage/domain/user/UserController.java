package dev.vaem.cloudstorage.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/users/me")
    public User getMe() {
        return userService.getMe();
    }

    @PostMapping("/users")
    public void addUser(@RequestBody UserRequest userRequest) {
        userService.addUser(userRequest);
    }

    @PutMapping("/users/me")
    public void updateUser(@RequestBody UserRequest userRequest) {
        userService.updateMe(userRequest);
    }

}
