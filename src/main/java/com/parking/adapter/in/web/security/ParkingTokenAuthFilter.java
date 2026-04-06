package com.parking.adapter.in.web.security;

import com.parking.adapter.out.persistence.entity.ParkingApiTokenEntity;
import com.parking.repository.ParkingApiTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ParkingTokenAuthFilter extends OncePerRequestFilter {

  private static final String TOKEN_HEADER = "X-API-Token";
  private static final Pattern PARKING_ID_PATTERN = Pattern.compile("^/api/parkings/(\\d+)/");

  private final ParkingApiTokenRepository parkingApiTokenRepository;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return !PARKING_ID_PATTERN.matcher(path).find();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = request.getHeader(TOKEN_HEADER);
    if (token == null || token.isBlank()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing X-API-Token header");
      return;
    }

    Optional<ParkingApiTokenEntity> tokenEntity = parkingApiTokenRepository.findByToken(token);
    if (tokenEntity.isEmpty()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API token");
      return;
    }

    String path = request.getRequestURI();
    Matcher matcher = PARKING_ID_PATTERN.matcher(path);
    if (matcher.find()) {
      Integer pathParkingId = Integer.valueOf(matcher.group(1));
      if (!tokenEntity.get().getParkingId().equals(pathParkingId)) {
        response.sendError(
            HttpServletResponse.SC_FORBIDDEN, "Token does not belong to this parking");
        return;
      }
    }

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            tokenEntity.get().getParkingId(), null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
