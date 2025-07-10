package io.github.codergod1337.streamplay.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name; // z. B. "nicht mehr", "rage quit"

    @ManyToOne(optional = false)
    private User author;

    @ManyToOne(optional = false)
    private User moderator;


    @ElementCollection
    @CollectionTable(name = "phrase_rawdata_ids", joinColumns = @JoinColumn(name = "phrase_id"))
    @Column(name = "rawdata_id")
    @OrderColumn(name = "position") // sichert Reihenfolge
    private List<Long> rawDataIds = new ArrayList<>();

    private long triggerCount = 0;

    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}
