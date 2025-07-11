package io.github.codergod1337.streamplay.service;


import io.github.codergod1337.streamplay.enums.UsableState;
import io.github.codergod1337.streamplay.model.PlayableEvent;
import io.github.codergod1337.streamplay.model.RawData;
import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.repository.RawDataRepository;
import io.github.codergod1337.streamplay.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RawDataService {

    private final RawDataRepository rawDataRepository;
    private final UserService userService;
    private final PlayableEventService playableEventService;

    public RawDataService(RawDataRepository rawDataRepository, UserService userService, PlayableEventService playableEventService) {
        this.rawDataRepository = rawDataRepository;
        this.userService = userService;
        this.playableEventService = playableEventService;
    }


    public Optional<RawData> findById(Long id) {
        return rawDataRepository.findById(id);
    }

    public List<RawData> findAll() {
        return rawDataRepository.findAll();
    }

    public void delete(Long id) {
        rawDataRepository.deleteById(id);
    }


    public RawData save(RawData rawDataToSave) {
    // can cause errors! because value is unique and not checkt for existence
        return rawDataRepository.save(rawDataToSave);
    }

    public RawData saveOrIncrement(RawData incoming) {
        return rawDataRepository.findByValue(incoming.getValue())
                .map(existing -> {
                    Long count = existing.getUsageCounter() != null
                            ? existing.getUsageCounter() + 1
                            : 1L;
                    existing.setUsageCounter(count);
                    return rawDataRepository.save(existing);
                })
                .orElseGet(() -> {
                    // Erster Eintrag für dieses Wort
                    incoming.setUsageCounter(1L);
                    return rawDataRepository.save(incoming);
                });
    }


    public void saveSentence(String sentence, String userName) {

        Optional<User> authorOpt = userService.findByUserName(userName);
        if (authorOpt.isEmpty()) {
            // Abbrechen: kein User mit diesem Namen gefunden
            throw new IllegalArgumentException("Unbekannter User: " + userName);
        }
        User author = authorOpt.get();

        // 1–3: Normalisieren & splitten
        String[] words = sentence
                .toLowerCase()                                          // Groß → Klein
                .replaceAll("[^\\p{L}\\s]+", " ")     // alles außer Buchstaben und Whitespace → Leerzeichen
                .trim()                                                 // führende/trailing Leerzeichen weg
                .split("\\s+");                                  // split an einem oder mehreren Whitespaces

        // 4: Jedes Wort weiterverarbeiten -> speichern und als gesprochnenes registrieren
        for (String word : words) {
            // 1. Suche nach vorhandenem RawData
            Optional<RawData> existingOpt = rawDataRepository.findByValue(word);

            RawData rawData;
            if (existingOpt.isPresent()) {
                // 2a. Existiert schon → Counter um 1 erhöhen
                rawData = existingOpt.get();
                Long current = rawData.getUsageCounter() != null
                        ? rawData.getUsageCounter()
                        : 0L;
                rawData.setUsageCounter(current + 1);

            } else {
                // 2b. Noch nicht existent → neues RawData anlegen
                rawData = new RawData();
                rawData.setValue(word);
                rawData.setType(RawData.DataType.TEXT);
                rawData.setUsableState(UsableState.APPROVED);
                rawData.setUsageCounter(1L);
                rawData.setModeratedBy(author);

            }

            // 3. Speichern (neu oder updated)
            rawDataRepository.save(rawData);
        }

    }
}
