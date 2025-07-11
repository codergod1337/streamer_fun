package io.github.codergod1337.streamplay.model;


import jakarta.persistence.*;
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
public class PlayableEvent {

    public enum PlayableEventStatus {
        OPENING,        // not joinable! choosing grid etc...
        CREATED,        // joinable
        RUNNING,
        FINISHED,
        CANCELLED       // no evaluation, all gamedata needs to be deleted
    }
    public enum GridSize {
        ONE(1), THREE(3), FIVE(5), SEVEN(7);

        private final int size;
        GridSize(int size) { this.size = size; }
        public int getSize() { return size; }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PlayableEventStatus playableEventStatus = PlayableEventStatus.OPENING;

    @ManyToMany
    @JoinTable(
            name = "playable_event_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> players = new ArrayList<>();

    private Instant creationTime;

    private Instant finishTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private GridSize gridSize = GridSize.SEVEN; // Default 7×7

    /**
     * Verknüpfung zu PatternData: welche wortketten pro gridkoordinate pro spieler bei 7x7 -> 49 einträge pro spieler
     */
    @OneToMany(
            mappedBy = "playableEvent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PatternData> patternDataList = new ArrayList<>();

}
