package com.luis.sistema_rotas.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@Service
public class TokenService {

    @Value("${jwt.security.token.secret}")
    private String secret;

    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("sistema-rotas")
                    .withSubject(user.getUsername())
                    .withClaim("id", user.getId().toString())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException ex){
            throw new RuntimeException("Error while generating token", ex);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
                    return JWT.require(algorithm)
                            .withIssuer("sistema-rotas")
                            .build()
                            .verify(token)
                            .getSubject();
        } catch (Exception ex){
            return "";
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now()
                .plusDays(7)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
