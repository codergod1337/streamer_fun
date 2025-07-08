package io.github.codergod1337.streamplay.controller;

import io.github.codergod1337.streamplay.model.UserGroup;
import io.github.codergod1337.streamplay.service.UserGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class UserGroupController {

    private final UserGroupService userGroupService;

    @Autowired
    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @PostMapping
    public ResponseEntity<UserGroup> createUserGroup(@Valid @RequestBody UserGroup userGroup) {
        UserGroup createdUserGroup = userGroupService.create(userGroup);
        return new ResponseEntity<>(createdUserGroup, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGroup> getUserGroupById(@PathVariable Long id) {
        return userGroupService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<UserGroup> getAllUserGroups() {
        return userGroupService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        userGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
