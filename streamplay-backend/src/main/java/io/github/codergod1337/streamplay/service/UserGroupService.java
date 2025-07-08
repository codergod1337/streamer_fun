package io.github.codergod1337.streamplay.service;



import io.github.codergod1337.streamplay.model.UserGroup;
import io.github.codergod1337.streamplay.repository.UserGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserGroupService {

    private final UserGroupRepository repository;

    @Autowired
    public UserGroupService(UserGroupRepository repository) {
        this.repository = repository;
    }

    public UserGroup create(UserGroup userGroup) {
        return repository.save(userGroup);
    }

    public Optional<UserGroup> findById(Long id) {
        return repository.findById(id);
    }

    public List<UserGroup> findAll() {
        return repository.findAll();
    }

    public Optional<UserGroup> findByGroupCode(String groupCode) {
        return repository.findByGroupCode(groupCode);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
