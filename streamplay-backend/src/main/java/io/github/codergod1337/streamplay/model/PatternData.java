package io.github.codergod1337.streamplay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int row;

    private int col;

    @ManyToOne(optional = false)
    private User owner;

    // 🧩 Die zugeordnete Phrase, die getroffen werden muss
    @ManyToOne(optional = false)
    @JoinColumn(name = "phrase_id", nullable = false)
    private Phrase phrase;

    // Das Spiel, in dem dieses Bingofeld aktiv ist
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private PlayableEvent playableEvent;

    @Builder.Default
    private boolean isCalled = false;
}
