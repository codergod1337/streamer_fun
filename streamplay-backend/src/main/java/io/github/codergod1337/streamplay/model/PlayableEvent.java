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
        CREATED, RUNNING, FINISHED, CANCELLED
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PlayableEventStatus playableEventStatus;

    @ManyToMany
    @JoinTable(
            name = "playable_event_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> players = new ArrayList<>();

    private Instant creationTime;

    private Instant finishTime;

    @OneToMany(mappedBy = "playableEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventData> eventDataList = new ArrayList<>();

}
