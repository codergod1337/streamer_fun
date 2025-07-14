package io.github.codergod1337.streamplay.controller;
import io.github.codergod1337.streamplay.dto.LoginRequest;
import io.github.codergod1337.streamplay.model.UserGroup;
import io.github.codergod1337.streamplay.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.service.UserService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Controller für Authentifizierung und Token-Ausgabe
 */
@RestController
@RequestMapping("/jwt")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping()
    public ResponseEntity<String> getToken(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String ip = request.getRemoteAddr();  // oder:   request.getHeader("X-Forwarded-For");
        String token = authService.authenticateAndCreateToken(loginRequest.getUserName(), loginRequest.getPassword(), ip);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/myroles")
    public ResponseEntity<List<String>> getAllRoles( @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 1) Header prüfen
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7); //- "Bearer "

        // 2) Rollen aus Token lesen
        List<String> groups = authService.getUserGroupsFromToken(token);

        // 3) Ergebnis zurückliefern
        return ResponseEntity.ok(groups);
    }

}