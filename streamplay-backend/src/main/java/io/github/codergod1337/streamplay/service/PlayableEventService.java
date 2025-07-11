package io.github.codergod1337.streamplay.service;

import io.github.codergod1337.streamplay.model.PlayableEvent;
import io.github.codergod1337.streamplay.repository.PlayableEventRepository;
import io.github.codergod1337.streamplay.repository.UserGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class PlayableEventService {


    private final PlayableEventRepository playableEventRepository;

    @Autowired
    public PlayableEventService(PlayableEventRepository playableEventRepository) {
        this.playableEventRepository = playableEventRepository;
    }
    
    public Optional<PlayableEvent> getActivePlayableEvent() {

        Optional<PlayableEvent> active = playableEventRepository.findActiveEvent();
        if (active.isPresent()) {
            return active;
        } else {
            PlayableEvent newEvent = PlayableEvent.builder()
                    .playableEventStatus(PlayableEvent.PlayableEventStatus.RUNNING)
                    .creationTime(Instant.now())
                    .players(new ArrayList<>())
                    .build();
            return Optional.ofNullable(save(newEvent));
        }
    }

    public PlayableEvent save(PlayableEvent event) {
        return playableEventRepository.save(event);
    }
    
}
