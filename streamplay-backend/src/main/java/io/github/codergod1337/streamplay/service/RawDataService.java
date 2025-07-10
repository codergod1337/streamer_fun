package io.github.codergod1337.streamplay.service;


import io.github.codergod1337.streamplay.model.RawData;
import io.github.codergod1337.streamplay.repository.RawDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RawDataService {

    private final RawDataRepository rawDataRepository;

    public RawDataService(RawDataRepository rawDataRepository) {
        this.rawDataRepository = rawDataRepository;
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



}
