package io.github.codergod1337.streamplay.initialization;



import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.model.UserGroup;
import io.github.codergod1337.streamplay.model.UserGroupAllocation;
import io.github.codergod1337.streamplay.repository.UserGroupRepository;
import io.github.codergod1337.streamplay.repository.UserRepository;
import io.github.codergod1337.streamplay.repository.UserGroupAllocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final UserGroupAllocationRepository userGroupAllocationRepository;
    @Value("${admin.password}")
    private String adminPassword;

    public DatabaseInitializer(UserGroupRepository userGroupRepository, UserRepository userRepository, UserGroupAllocationRepository userGroupAllocationRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.userGroupAllocationRepository = userGroupAllocationRepository;
    }
    @Override
    public void run(String... args) {

        // Liste aller Gruppen, die erstellt werden sollen
        List<UserGroup> groupsToCreate = List.of(
                new UserGroup(null, "Superadmin", "ROLE_SUPERADMIN", "Vollzugriff auf System und Usermanagement"),
                new UserGroup(null, "Administrator", "ROLE_ADMIN", "Administrativer Zugriff"),
                new UserGroup(null, "Moderator", "ROLE_MODERATOR", "Moderation von Inhalten und Benutzern"),
                new UserGroup(null, "VIP", "ROLE_VIP", "Besondere Benutzer mit erweiterten Rechten"),
                new UserGroup(null, "Benutzer", "ROLE_USER", "Standardnutzer ohne Sonderrechte")
        );

        for (UserGroup group : groupsToCreate) {
            userGroupRepository.findByGroupCode(group.getGroupCode())
                    .orElseGet(() -> userGroupRepository.save(group));
        }

        // 2. Admin-User anlegen
        User adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("admin");
                    u.setPassword(adminPassword); // Nur für Entwicklung – später Passwort hashen!
                    return userRepository.save(u);
                });

        // 3. Gruppenzuweisung prüfen und ggf. zuweisen
        boolean alreadyAssigned = userGroupAllocationRepository.existsByUserAndUserGroup_GroupCode(adminUser, "ROLE_ADMIN");

        // Admin-User der Gruppe ROLE_ADMIN zuweisen
        boolean assigned = userGroupAllocationRepository.existsByUserAndUserGroup_GroupCode(adminUser, "ROLE_ADMIN");
        if (!assigned) {
            UserGroup adminGroup = userGroupRepository.findByGroupCode("ROLE_ADMIN").orElseThrow();
            UserGroupAllocation userGroupAllocation = new UserGroupAllocation();
            userGroupAllocation.setUser(adminUser);
            userGroupAllocation.setUserGroup(adminGroup);
            userGroupAllocation.setGrantedBy(adminUser);
            userGroupAllocation.setAssignedAt(Instant.now());
            userGroupAllocationRepository.save(userGroupAllocation);
        }


    }
}