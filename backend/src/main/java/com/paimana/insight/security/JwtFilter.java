package com.paimana.insight.security;
import com.paimana.insight.repository.UserRepository; import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
@Component public class JwtFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final UserRepository users;
 public JwtFilter(JwtService j,UserRepository u){jwt=j;users=u;}
 protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain c)throws ServletException,IOException{
  String h=r.getHeader("Authorization"); if(h!=null&&h.startsWith("Bearer ")){String t=h.substring(7);if(jwt.valid(t))users.findByEmail(jwt.email(t)).ifPresent(u->SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u.getEmail(),null,java.util.List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole())))));}
  c.doFilter(r,s);
 }
}
