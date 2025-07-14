package io.github.codergod1337.streamplay.controller;


import io.github.codergod1337.streamplay.dto.UserNameRequest;
import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/fulluser")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.ok(created);
    }
    @PostMapping
    public ResponseEntity<User> createUserByUserName(@RequestHeader(value = "Authorization", required = false) String authHeader,  @RequestBody Map<String,String> body) {
        // TODO: Auth Requester!!!
        if (body.get("userName").isEmpty() || userService.usernameExists(body.get("userName"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.createUserByUserName(body.get("userName")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/name")
    public ResponseEntity<User> getUserByUsername(@RequestBody UserNameRequest request) {
        Optional<User> optionalUser = (Optional<User>) userService.findByUserName(request.getUserName());
        return optionalUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        List<User> all = userService.findAll();
        return ResponseEntity.ok(all);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
