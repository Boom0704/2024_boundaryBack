package org.example.boundaryback.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public UserController(UserService userService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @PreAuthorize("permitAll()") // 인증 없이 접근 가능
  @GetMapping("/check")
  public String check() {
    return "is checked";
  }

  @PreAuthorize("permitAll()") // 인증 없이 접근 가능
  @PostMapping("/signup")
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    System.out.println("Received user data: " + user);
    try {
      userService.registerUser(user);
      return ResponseEntity.ok("User registered successfully!");
    } catch (Exception e) {
      System.err.println("User registration failed: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
    }
  }

  @PreAuthorize("permitAll()")
  @PostMapping("/signin")
  public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
    String username = loginData.get("username");
    String password = loginData.get("password");

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password)
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 세션 강제 생성 및 SecurityContext 설정
      HttpSession session = request.getSession(true); // 세션이 없으면 새로 생성
      session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

      System.out.println("로그인 성공, 세션 ID: " + session.getId());

      // 유저 정보 조회
      User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

      // 응답 생성
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Login successful!");
      response.put("sessionId", session.getId());
      response.put("user", user); // 유저 정보 추가

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      System.out.println("Invalid login attempt");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
    }
  }



  // ID로 유저 조회
  @GetMapping("/{id}")
  public Optional<User> getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  // 모든 유저 조회
  @GetMapping("/all")
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  // 유저 삭제
  @DeleteMapping("/{id}")
  public String deactivateUser(@PathVariable("id") Long id, HttpServletRequest request) {
    userService.deactivateUser(id);

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
    return "User deactivated and session invalidated successfully!";
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/sessionCheck")
  public ResponseEntity<Map<String, Object>> sessionCheck(HttpServletRequest request, @RequestHeader Map<String, String> headers) {
    HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
    if (session == null) {
      System.out.println("현재 요청에 연결된 세션이 없습니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
    }

    System.out.println("세션 ID: " + session.getId());

    // 인증 객체 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isAuthenticated = authentication != null
        && !(authentication instanceof AnonymousAuthenticationToken); // 익명 인증이 아닌지 확인

    // 요청 헤더 출력
    System.out.println("==== 요청 헤더 ====");
    headers.forEach((key, value) -> System.out.println(key + ": " + value));

    // 인증 객체 정보 출력
    if (authentication != null) {
      System.out.println("인증 객체 타입: " + authentication.getClass().getSimpleName());
      System.out.println("인증된 유저 여부: " + isAuthenticated);
      System.out.println("현재 사용자 이름: " + authentication.getName());
      System.out.println("권한 정보: " + authentication.getAuthorities());
      System.out.println("인증 객체 상세 정보: " + authentication.getDetails());
    } else {
      System.out.println("인증 객체가 존재하지 않습니다.");
    }

    // 인증이 유효하지 않으면 `authenticated` 상태만 반환
    if (!isAuthenticated) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
    }

    // 인증된 유저 정보 조회
    String username = authentication.getName(); // 인증된 유저의 이름을 가져옴
    Optional<User> userOptional = userService.getUserByUsername(username); // 유저 정보 조회

    if (userOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
    }

    // 인증이 유효하고 유저 정보가 조회되면 추가로 유저 정보를 포함하여 반환
    Map<String, Object> response = new HashMap<>();
    response.put("authenticated", true);
    response.put("user", userOptional.get()); // 유저 정보 포함

    return ResponseEntity.ok(response);
  }


  @PostMapping("/logout")
  public ResponseEntity<String> logoutUser(HttpServletRequest request) {
    // 세션을 무효화하여 인증 정보 제거
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    SecurityContextHolder.clearContext();
    return ResponseEntity.ok("Logout successful!");
  }
}
