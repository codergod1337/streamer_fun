package io.github.codergod1337.streamplay.service;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public AuthService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Erzeugt ein JWT mit Username und Rollen als Claims.
     */
    public String createToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("streamplay-backend")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("roles", roles)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    /**
     * Prüft, ob das gegebene JWT den angegebenen Role-Claim enthält.
     */
    public boolean hasRole(String token, String requiredRole) {
        var jwt = jwtDecoder.decode(token);
        var roles = jwt.getClaimAsStringList("roles");
        return roles != null && roles.contains(requiredRole);
    }

    // Weitere Helfer: extractUsername, extractExpiration, validateToken, etc.
}