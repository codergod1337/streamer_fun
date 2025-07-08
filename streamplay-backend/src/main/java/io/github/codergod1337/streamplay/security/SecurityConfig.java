package io.github.codergod1337.streamplay.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@PropertySource("file:${user.dir}/_CONFIDENTIAL/secret.properties")  // lädt jwt.secret aus externem File  // Aktiviert Spring Security
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;
    //private static final String SECRET = "lL4jQ1hDS25rNmYxb0hVTkVscE1qT2lXZ05EeVZCZ0E";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // a) stateless REST-API, kein CSRF
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // b) Zugriffsregeln
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",    // OpenAPI-JSON
                                "/swagger-ui.html",   // Swagger UI
                                "/swagger-ui/**",     // Swagger UI Assets
                                "/webjars/**",        // Webjar-Assets (falls genutzt)
                                "/api/auth/**",       // Auth-Endpoints
                                "/api/*"              // TEMP WILL BE REMOVED SOON ;)
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // c) Resource Server: automatisch Bearer-Filter + JwtAuthenticationProvider
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // 1) Baue SecretKey für HMAC-SHA256
        SecretKey key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        // 2) Wrappe ihn in einen JWK und markiere Algorithmus
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(key)
                .algorithm(JWSAlgorithm.HS256)
                .build();

        // 3) Erzeuge eine JWK-Quelle aus diesem einen Key
        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(new JWKSet(jwk));

        // 4) Initialisiere und gib den NimbusJwtEncoder zurück
        return new NimbusJwtEncoder(jwkSource);
    }

}