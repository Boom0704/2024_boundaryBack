package org.example.boundaryback.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.boundaryback.hashtag.Hashtag;
import org.example.boundaryback.post.Post;
import org.example.boundaryback.post.PostService;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final PostService postService;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public UserController(UserService userService, PostService postService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.postService = postService;
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

  @GetMapping("/detail/{username}")
  public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
    try {
      // 유저 기본 정보 조회
      User user = userService.getUserByUsername(username)
          .orElseThrow(() -> new RuntimeException("User not found"));

      // 친구 목록 조회 (필요한 정보만 선택)
      Set<User> friends = userService.getFriendsByUserId(user.getId()).stream()
          .map(friend -> {
            User friendInfo = new User();
            friendInfo.setId(friend.getId());
            friendInfo.setUsername(friend.getUsername());
            friendInfo.setProfilePictureUrl(friend.getProfilePictureUrl());
            return friendInfo;
          })
          .collect(Collectors.toSet());

      // 유저가 작성한 게시글 조회 (필요한 정보만 선택)
      // 유저가 작성한 게시글 조회 (필요한 정보만 선택)
      List<Post> posts = postService.getPostsByUser(user);

      // 게시글에서 해시태그 추출 후 빈도 계산
      Map<String, Integer> hashtagCount = new HashMap<>();
      for (Post post : posts) {
        if (post.getHashtags() != null) {  // null 체크 추가
          for (Hashtag hashtag : post.getHashtags()) {
            String tag = hashtag.getName();
            hashtagCount.put(tag, hashtagCount.getOrDefault(tag, 0) + 1);
          }
        }
      }


      // 게시글에서 해시태그 제외하고 필요한 정보만 정리
      List<Post> cleanedPosts = posts.stream()
          .map(post -> {
            Post postInfo = new Post();
            postInfo.setId(post.getId());
            postInfo.setImageUrls(post.getImageUrls());  // 게시글의 이미지 URL만 전달
            return postInfo;
          })
          .toList();


      // 응답 데이터 구성
      Map<String, Object> response = new HashMap<>();
      response.put("user", user);  // 유저 정보 전체
      response.put("friends", friends);  // 친구 정보 (필요한 정보만)
      response.put("cleanedPosts", cleanedPosts);  // 게시글 정보 (id, imageUrls만)
      response.put("hashtagCount", hashtagCount);  // 해시태그 빈도

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("message", "Failed to fetch user data"));
    }
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

  @GetMapping("/{username}/profile-image")
  public ResponseEntity<String> getUserProfileImage(@PathVariable String username) {
    Optional<String> profileImageUrl = userService.getUserProfileImageUrlByUsername(username);
    if (profileImageUrl.isPresent()) {
      return ResponseEntity.ok(profileImageUrl.get());
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile image not found for user with username: " + username);
    }
  }

  // 사용자 정보 업데이트
  @PutMapping("/{id}/update")
  public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO updateRequest) {
    try {
      System.out.println("Update request received for user ID: " + id);
      System.out.println("Username: " + updateRequest.getUsername());
      System.out.println("Password: " + updateRequest.getPassword());
      System.out.println("Profile Picture URL: " + updateRequest.getProfilePictureUrl());

      userService.updateUser(id, updateRequest);
      return ResponseEntity.ok("User updated successfully!");
    } catch (Exception e) {
      e.printStackTrace(); // 예외 출력
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User update failed: " + e.getMessage());
    }
  }
}
