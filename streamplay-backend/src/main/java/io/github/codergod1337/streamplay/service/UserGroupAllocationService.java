package io.github.codergod1337.streamplay.service;


import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.model.UserGroup;
import io.github.codergod1337.streamplay.model.UserGroupAllocation;
import io.github.codergod1337.streamplay.repository.UserGroupAllocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserGroupAllocationService {
    private final UserGroupAllocationRepository repository;

    @Autowired
    public UserGroupAllocationService(UserGroupAllocationRepository repository) {
        this.repository = repository;
    }

    /**
     * Zuweisung einer Rolle zu einem User.
     */
    public UserGroupAllocation assignRoleToUser(User user, UserGroup userGroup, User grantedBy) {
        UserGroupAllocation userGroupAllocation = new UserGroupAllocation();
        userGroupAllocation.setUser(user);
        userGroupAllocation.setUserGroup(userGroup);
        userGroupAllocation.setGrantedBy(grantedBy);
        userGroupAllocation.setAssignedAt(Instant.now());

        return repository.save(userGroupAllocation);
    }

    /**
     * Rolle von User entfernen.
     */
    public void removeRoleFromUser(Long userRoleId) {
        repository.deleteById(userRoleId);
    }

    /**
     * Alle Rollen eines Users abrufen.
     */
    public List<UserGroupAllocation> findRolesByUser(User user) {
        return repository.findAllByUser(user);
    }

    /**
     * Prüfen, ob User eine bestimmte Rolle hat.
     */
    public boolean userHasUserGroup(User user, String groupName) {
        return repository.existsByUserAndUserGroup_GroupCode(user, groupName);
    }

    public List<UserGroupAllocation> findAll() {
        return repository.findAll();
    }

    public Optional<UserGroupAllocation> findById(Long id) {
        return repository.findById(id);
    }
}
