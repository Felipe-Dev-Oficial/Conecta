package com.etec.zl.conecta.Infraestructure.Security.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    @Value("${JWT_SECRET}")
    private String secret;

    private Instant expirationDate() {
        return Instant.now().plusSeconds(604800);
    }


    public String generateToken(User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("conecta-app")
                    .withSubject(user.getId())
                    .withClaim("user_nome", user.getNome().name())
                    .withClaim("user_role", user.getTipo().toString())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTVerificationException exception) {
            var e = new ValidationFailedException("Erro ao gerar JWT");
            log.error(e.getMessage(), exception);
            throw e;
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("conecta-app")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public String getClaim(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();
    }
}
