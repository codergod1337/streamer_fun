package io.github.codergod1337.streamplay.repository;


import io.github.codergod1337.streamplay.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {


    Optional<UserGroup> findByGroupCode(String roleAdmin);
}
