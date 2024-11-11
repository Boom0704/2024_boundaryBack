package org.example.boundaryback.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    // 현재 인증된 사용자 정보를 가져옴
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증 객체가 null이거나 인증되지 않은 상태인 경우에만 401 반환
    if (authentication == null || !authentication.isAuthenticated()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    } else {
      // 세션이 유효할 경우, 인증이 필요한 리소스에 접근하려는 동작을 허용
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden - Access Denied");
    }
  }
}
