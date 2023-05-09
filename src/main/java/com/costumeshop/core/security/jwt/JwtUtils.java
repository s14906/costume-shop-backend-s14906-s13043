package com.costumeshop.core.security.jwt;

import com.costumeshop.core.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature: " + e.getMessage(), e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT token is expired: " + e.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("JWT token is unsupported: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT claims string is empty: " + e.getMessage(), e);
        }
    }
}
