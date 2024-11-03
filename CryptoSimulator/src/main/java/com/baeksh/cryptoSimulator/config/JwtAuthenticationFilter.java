package com.baeksh.cryptoSimulator.config;

import com.baeksh.cryptoSimulator.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    
    String jwt = getJwtFromRequest(request);

    if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
      Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userId, null, jwtTokenProvider.getAuthoritiesFromToken(jwt));
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); //Bearer 의 공백까지 포함해서 7
    }
    
    return null;
  }
}

/*에러
//https://buly.kr/4mba4Sx
*/
