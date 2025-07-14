package io.github.codergod1337.streamplay.repository;

import io.github.codergod1337.streamplay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    Optional<User> findById(Long id);

}
