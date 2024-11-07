package org.example.boundaryback.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
  public String registerUser(@RequestBody User user) {
    userService.registerUser(user);
    System.out.println(user);
    return "User registered successfully!";
  }

  @PreAuthorize("permitAll()") // 인증 없이 접근 가능
  @PostMapping("/signin")
  public String loginUser(@RequestBody Map<String, String> loginData) {
    System.out.println("loginData: " + loginData);

    String username = loginData.get("username");
    String password = loginData.get("password");

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password)
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return "Login successful!";
    } catch (Exception e) {
      return "Invalid username or password";
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
  public String deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return "User deleted successfully!";
  }

  // 세션 체크 엔드포인트
  @GetMapping("/sessionCheck")
  public Map<String, Boolean> sessionCheck() {
    boolean isAuthenticated = userService.isAuthenticated();
    return Map.of("authenticated", isAuthenticated);
  }
}
