package io.github.codergod1337.streamplay.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                                               // Generiert Getter, Setter, toString, equals und hashCode
@NoArgsConstructor                                  // Generiert einen No-Args-Konstruktor
@AllArgsConstructor                                 // Generiert einen All-Args-Konstruktor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;


    @NotBlank
    @Column(nullable = false)
    private String password;           // gehashtes Passwort

    @Column(name = "points_account")
    private Long pointsAccount = 0L; // Punktekonto des Users

    @Column(name = "online_status")
    private String onlineStatus = "offline"; // Online-Status

    @Column(name = "warnings")
    private Integer warnings = 0;      // Anzahl Verwarnungen

    // TODO: Beziehungen zu Kommentaren etc.

}
