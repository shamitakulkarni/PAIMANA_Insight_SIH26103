package com.paimana.insight.security;
import com.paimana.insight.model.User; import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.util.Date;
@Service public class JwtService {
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${app.jwt.secret}")String s,@Value("${app.jwt.expiration-ms}")long e){if(s.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalArgumentException("JWT secret too short");key=Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));expiration=e;}
 public String generate(User u){Date n=new Date();return Jwts.builder().subject(u.getEmail()).claim("role",u.getRole().name()).claim("name",u.getName()).issuedAt(n).expiration(new Date(n.getTime()+expiration)).signWith(key).compact();}
 public String email(String t){return Jwts.parser().verifyWith(key).build().parseSignedClaims(t).getPayload().getSubject();}
 public boolean valid(String t){try{Jwts.parser().verifyWith(key).build().parseSignedClaims(t);return true;}catch(Exception e){return false;}}
}
