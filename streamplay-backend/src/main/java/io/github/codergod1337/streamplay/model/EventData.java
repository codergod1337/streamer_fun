package io.github.codergod1337.streamplay.model;

import jakarta.persistence.*;

@Entity
public class EventData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;  // z. B. "gridSize", "entry", "match"

    private String value; // kann bei Bedarf auch JSON sein

    @ManyToOne
    private PlayableEvent playableEvent;
}
