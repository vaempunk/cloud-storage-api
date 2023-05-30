package dev.vaem.cloudstorage.util;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dev.vaem.cloudstorage.config.JwtProperties;
import dev.vaem.cloudstorage.domain.token.TokenPair;
import dev.vaem.cloudstorage.domain.user.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtility {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    private long accessTokenExp;
    private long refreshTokenExp;

    public JwtUtility(JwtProperties jwtProperties) {
        this.privateKey = jwtProperties.getPrivateKey();
        this.publicKey = jwtProperties.getPublicKey();
        this.accessTokenExp = jwtProperties.getAccessTokenDuration().toMillis();
        this.refreshTokenExp = jwtProperties.getRefreshTokenDuration().toMillis();
    }

    public TokenPair generateTokenPair(UserAccount userAccount) {
        return new TokenPair(
                generateAccessToken(userAccount),
                generateRefreshToken(userAccount));
    }

    public TokenPair refreshTokens(String refreshToken) {
        var userAccount = parseAccessToken(refreshToken);
        if (userAccount == null) {
            return null;
        }
        return new TokenPair(
                generateAccessToken(userAccount),
                generateRefreshToken(userAccount));
    }

    public String generateAccessToken(UserAccount userAccount) {
        return Jwts.builder()
                .setSubject(userAccount.getId())
                .claim("scope", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExp))
                .claim("roles", userAccount.getRoles())
                .signWith(privateKey)
                .compact();
    }

    public String generateRefreshToken(UserAccount userAccount) {
        return Jwts.builder()
                .setSubject(userAccount.getId())
                .claim("scope", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + refreshTokenExp))
                .claim("roles", userAccount.getRoles())
                .signWith(privateKey)
                .compact();
    }

    public UserAccount parseAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.getExpiration().before(new Date()) || !"access".equals(claims.get("scope", String.class))) {
                return null;
            }
            List<?> rolesObj = claims.get("roles", List.class);
            return UserAccount.builder()
                    .id(claims.getSubject())
                    .roles(rolesObj.stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet()))
                    .build();
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
