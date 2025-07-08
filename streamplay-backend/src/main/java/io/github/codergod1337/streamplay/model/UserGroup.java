package io.github.codergod1337.streamplay.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_groups")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "display_name", unique = true, nullable = false, length = 80)
    private String displayName;          // z.B. "admin"

    @Column(name = "group_code", unique = true, nullable = false, length = 40)
    private String groupCode;          // z.B. "ROLE_ADMIN"

    @Column(name = "group_description", columnDefinition = "TEXT")
    private String groupDescription;   // ausführliche Beschreibung
}
