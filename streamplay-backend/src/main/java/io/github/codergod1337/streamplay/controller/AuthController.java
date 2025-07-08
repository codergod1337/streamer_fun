package io.github.codergod1337.streamplay.controller;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Controller für Authentifizierung und Token-Ausgabe
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SECRET = "lL4jQ1hDS25rNmYxb0hVTkVscE1qT2lXZ05EeVZCZ0E";

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        Date issuedAt  = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(3600));

        // Builder mit claim(...) statt setSubject/etc. und signWith(key)
        String jwt = Jwts.builder()
                .claim("sub", "demo-user")
                .claim("iss", "streamplay-backend")
                .claim("iat", issuedAt)
                .claim("exp", expiresAt)
                .signWith(key)          // nutzt automatisch HS256
                .compact();

        return ResponseEntity.ok(jwt);
    }

}