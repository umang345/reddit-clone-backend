package com.umang345.redditclonebackend.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static io.jsonwebtoken.Jwts.parser;

/***
 * Contains service methods for generating JWT Token
 */
@Service
@RequiredArgsConstructor
public class JwtProvider
{
    private final JwtEncoder jwtEncoder;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

     private KeyStore keyStore;

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;



    /***
     * Generates JWT token
     * @param authentication :
     * @return return generated JWT token
     */
    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return generateTokenWithUserName(principal.getUsername());
    }

    public String generateTokenWithUserName(String username) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
                .subject(username)
                .claim("scope", "ROLE_USER")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }

    public boolean validateToken(String jwt)
    {
        parser().setSigningKey(publicKey).parseClaimsJws(jwt);
        return true;
    }

    public String getUsernameFromJwt(String jwt){
        Claims claims = parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(jwt)
                .getBody();

        return claims.getSubject();
    }

}

