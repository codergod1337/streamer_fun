package io.github.codergod1337.streamplay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_group_allocations")
public class UserGroupAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;               // Zuordnung zu User

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_group_id", nullable = false)
    private UserGroup userGroup;               // Zuordnung zu Rolle

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt = Instant.now(); // Zeitpunkt der Zuweisung

    @ManyToOne
    @JoinColumn(name = "granted_by_user_id")
    private User grantedBy;          // welcher User hat dies autorisiert

}
