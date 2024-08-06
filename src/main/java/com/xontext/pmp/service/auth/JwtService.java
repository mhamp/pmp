package com.xontext.pmp.service.auth;

import com.xontext.pmp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {

    // NOTE: Following this quickstart guide: https://github.com/jwtk/jjwt#installation
    // We need a signing key, so we'll create one just for this example. Usually
    // the key would be read from your application configuration instead.
//     @Value("${application.security.jwt.secret-key}")
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds: 1000 * 60 * 60 * 24
//    @Value("${application.security.jwt.expiration}")
//    private long jwtExpiration;
//    @Value("${application.security.jwt.refresh-token.expiration}")
//    private long refreshExpiration;
//    String jws = Jwts.builder().subject("Joe").signWith(SECRET_KEY).compact();

    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("userId", ((User) userDetails).getId());
        return createToken(claims, userDetails.getUsername());
    }
    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private static Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
//        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public static boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        return createToken(claims, user.getUsername());
    }
}
