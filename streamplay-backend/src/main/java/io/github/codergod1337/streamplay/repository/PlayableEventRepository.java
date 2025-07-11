package io.github.codergod1337.streamplay.repository;

import io.github.codergod1337.streamplay.model.PlayableEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayableEventRepository extends JpaRepository<PlayableEvent, Long> {

    /**
     * Liefert das aktuell laufende PlayableEvent (Status = RUNNING), falls vorhanden.
     */
    @Query("SELECT e FROM PlayableEvent e WHERE e.playableEventStatus = io.github.codergod1337.streamplay.model.PlayableEvent.PlayableEventStatus.RUNNING")
    Optional<PlayableEvent> findActiveEvent();
}
