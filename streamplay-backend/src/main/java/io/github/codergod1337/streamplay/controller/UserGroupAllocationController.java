package io.github.codergod1337.streamplay.controller;


import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.model.UserGroup;
import io.github.codergod1337.streamplay.model.UserGroupAllocation;
import io.github.codergod1337.streamplay.service.UserGroupService;
import io.github.codergod1337.streamplay.service.UserGroupAllocationService;
import io.github.codergod1337.streamplay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-roles")
public class UserGroupAllocationController {

    private final UserGroupAllocationService userGroupAllocationService;
    private final UserService userService;
    private final UserGroupService userGroupService;

    @Autowired
    public UserGroupAllocationController(UserGroupAllocationService userGroupAllocationService, UserService userService, UserGroupService userGroupService) {
        this.userGroupAllocationService = userGroupAllocationService;
        this.userService = userService;
        this.userGroupService = userGroupService;
    }

    @PostMapping("/assign")
    public ResponseEntity<UserGroupAllocation> assignRoleToUser(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            @RequestParam(required = false) Long grantedByUserId
    ) {
        Optional<User> userOpt = userService.findById(userId);
        Optional<UserGroup> roleOpt = userGroupService.findById(roleId);
        Optional<User> grantedByOpt = grantedByUserId != null
                ? userService.findById(grantedByUserId)
                : Optional.empty();

        if (userOpt.isEmpty() || roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserGroupAllocation assignedRole = userGroupAllocationService.assignRoleToUser(
                userOpt.get(),
                roleOpt.get(),
                grantedByOpt.orElse(null)
        );

        return new ResponseEntity<>(assignedRole, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGroupAllocation> getUserRoleById(@PathVariable Long id) {
        return userGroupAllocationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserGroupAllocation>> getRolesByUser(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<UserGroupAllocation> roles = userGroupAllocationService.findAllUserGroupsByUser(userOpt.get());
        return ResponseEntity.ok(roles);
    }

    @GetMapping
    public List<UserGroupAllocation> getAllUserRoles() {
        return userGroupAllocationService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUserRole(@PathVariable Long id) {
        userGroupAllocationService.removeRoleFromUser(id);
        return ResponseEntity.noContent().build();
    }
}
