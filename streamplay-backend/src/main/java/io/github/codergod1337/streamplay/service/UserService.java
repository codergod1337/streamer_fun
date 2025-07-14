package io.github.codergod1337.streamplay.service;

import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz123456789";
    private static final SecureRandom RNG = new SecureRandom();

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User create(User user) {
        return repository.save(user);
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Optional<User> findByUserName(String userName) {
        return repository.findByUserName(userName);
    }
    public boolean usernameExists(String userName) {return repository.existsByUserName(userName);}

    public User createUserByUserName(String userName) {
        if(repository.findByUserName(userName).isPresent()){
            return new User();
        }
        User user = new User();
        user.setUserName(userName);
        user.setPassword(generateRandomPassword(10));
        return repository.save(user);
    }


    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RNG.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }


}
