package io.github.codergod1337.streamplay.controller;

import io.github.codergod1337.streamplay.dto.RawDataTextDTO;
import io.github.codergod1337.streamplay.enums.UsableState;
import io.github.codergod1337.streamplay.model.RawData;
import io.github.codergod1337.streamplay.service.RawDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rawdata")
public class RawDataController {

    private final RawDataService rawDataService;


    public RawDataController(RawDataService rawDataService) {
        this.rawDataService = rawDataService;
    }

    @PostMapping("/text")
    public ResponseEntity<RawData> createRawData(@RequestBody Map<String, String> requestBody) {
        String value = requestBody.get("value");

        RawData rawDataToSave = RawData.builder()
                .value(value)
                .moderatedBy(null)
                .type(RawData.DataType.TEXT)
                .usableState(UsableState.UNCHECKED)
                .usageCounter(0L)
                .build();

        RawData saved = rawDataService.saveOrIncrement(rawDataToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(rawDataToSave);
    }


    @GetMapping
    public List<RawData> getAll() {
        return rawDataService.findAll();
    }



    @GetMapping("/{id}")
    public ResponseEntity<RawData> getById(@PathVariable Long id) {
        return rawDataService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rawDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
