package io.github.codergod1337.streamplay.repository;

import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.model.UserGroupAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupAllocationRepository extends JpaRepository<UserGroupAllocation, Long> {
    List<UserGroupAllocation> findAllByUser(User user);
    boolean existsByUserAndUserGroup_GroupCode(User user, String groupCode);

}
