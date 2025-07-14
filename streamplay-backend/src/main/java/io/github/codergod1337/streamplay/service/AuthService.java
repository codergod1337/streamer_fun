package io.github.codergod1337.streamplay.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import io.github.codergod1337.streamplay.model.User;
import io.github.codergod1337.streamplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;
    private final UserGroupAllocationService userGroupAllocationService;
    @Value("${jwt.key}")
    private String jwtKey;

    public AuthService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserService userService, UserGroupAllocationService userGroupAllocationService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
        this.userGroupAllocationService = userGroupAllocationService;
    }

    private String hashIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash IP", e);
        }
    }




    public boolean hasRole(String token, String requiredRole) {
        var jwt = jwtDecoder.decode(token);
        var roles = jwt.getClaimAsStringList("roles");
        return roles != null && roles.contains(requiredRole);
    }

    public String authenticateAndCreateToken(String username, String rawPassword, String ip) {
        // check for UserName in DB
        User user = (User) userService.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // check user's pw no hash so far! // TODO:
        if (!user.getPassword().equals(rawPassword)) {
            throw new RuntimeException("Invalid credentials");
        }
        // TODO: later ->String fingerprint = hashIp(ip);
        String fingerprint = ip.toString();

        List<String> groupCodes = userGroupAllocationService.findAllUserGroupsByUser(user).stream()
                .map(alloc -> alloc.getUserGroup().getGroupCode())
                .toList();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("streamplay-backend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(36000000)) // TODO: zeit wieder kürzen!
                .subject(username)
                .claim("userId", user.getId())
                .claim("userName", user.getUserName())
                .claim("userGroups", groupCodes)
                .claim("fingerprint", fingerprint)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .keyId(jwtKey)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        //return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).keyId("streamplay-hs256").build(), claims)).getTokenValue();
        //return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public List<String> getUserGroupsFromToken(String token) {
        var jwt = jwtDecoder.decode(token);
        var groups = jwt.getClaimAsStringList("userGroups");
        return groups != null ? groups : Collections.emptyList();
    }




    // Weitere Helfer: extractUsername, extractExpiration, validateToken, etc.
}