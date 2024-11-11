package org.example.boundaryback.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// SessionCookieInterceptor 설정 예시
@Component
public class SessionCookieInterceptor implements HandlerInterceptor {

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("JSESSIONID".equals(cookie.getName())) {
          cookie.setMaxAge(60 * 60 * 24 * 7); // 7일 설정
          cookie.setPath("/");
          response.addCookie(cookie); // 수정된 쿠키를 응답에 추가
        }
      }
    }
  }
}
