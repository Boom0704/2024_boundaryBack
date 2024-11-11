package org.example.boundaryback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 메서드 보안 활성화
public class SecurityConfig {

  @Value("${cors.allowed.origins}")
  private String[] allowedOrigins;

  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
    this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(auth -> auth
            // 정적 리소스는 인증 없이 접근 가능하게 설정
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            // 특정 엔드포인트는 인증 없이 접근 가능하게 설정
            .requestMatchers("/users/signup", "/users/signin", "/users/check", "/users/sessionCheck", "/common/create-test-file").permitAll()
            .anyRequest().authenticated())
        .formLogin(AbstractHttpConfigurer::disable) // Spring Security의 기본 로그인 폼 비활성화
//        .logout(logout -> logout.permitAll())
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(restAuthenticationEntryPoint)); // 401 응답 설정

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
