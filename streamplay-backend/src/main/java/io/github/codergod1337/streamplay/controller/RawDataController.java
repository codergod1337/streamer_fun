package io.github.codergod1337.streamplay.controller;

import io.github.codergod1337.streamplay.dto.RawDataTextDTO;
import io.github.codergod1337.streamplay.enums.UsableState;
import io.github.codergod1337.streamplay.model.RawData;
import io.github.codergod1337.streamplay.service.RawDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rawdata")
public class RawDataController {

    private final RawDataService rawDataService;
    public final JwtDecoder jwtDecoder;

    public RawDataController(RawDataService rawDataService, JwtDecoder jwtDecoder) {
        this.rawDataService = rawDataService;
        this.jwtDecoder = jwtDecoder;
    }

    @PostMapping("/text")
    public ResponseEntity<RawData> createRawData(@RequestBody Map<String, String> requestBody, @RequestHeader("Authorization") String authHeader) {

        // 1) incoming sentence
        String sentence = requestBody.get("value");

        // TODO: check what happens if requestBody.get("value") does not exist =)
        if (sentence == null || sentence.isBlank()) {} //TODO return 200!

        // 2) Token extrahieren und Usernamen lesen
        // if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // }

        String token = authHeader.substring(7);
        String userName = jwtDecoder.decode(token).getSubject();
        // TODO: check HAS_ROLE("admin")

        // 3) Ganze Logik im Service ausführen
        rawDataService.saveSentence(sentence, userName);

        return ResponseEntity.status(HttpStatus.CREATED).build();

        /* old part, but was working ;)
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

         */
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
