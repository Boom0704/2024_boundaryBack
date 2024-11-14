package org.example.boundaryback.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // 사용자 등록
  public void registerUser(User user) {
    if (user.getProfilePictureUrl() == null || user.getProfilePictureUrl().isEmpty()) {
      String defaultProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
          .path("/common/file/")
          .path("default.jpg") // The default profile image filename
          .toUriString();
      user.setProfilePictureUrl(defaultProfileUrl); // Set the dynamically generated URL
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setActive(true); // 기본적으로 활성화 상태로 설정
    userRepository.save(user);
  }

  // 로그인 검증
  public boolean verifyLogin(String username, String password) {
    Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
    if (userOpt.isEmpty()) {
      return false;
    }
    User user = userOpt.get();
    return passwordEncoder.matches(password, user.getPassword());
  }

  // ID로 활성 유저 조회
  public Optional<User> getUserById(Long id) {
    return userRepository.findByIdAndIsActiveTrue(id);
  }

  // 모든 활성 유저 조회
  public List<User> getAllUsers() {
    return userRepository.findByIsActiveTrue();
  }

  // 사용자 비활성화 (삭제 대신)
  public void deactivateUser(Long id) {
    Optional<User> userOpt = userRepository.findById(id);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      user.setActive(false); // 비활성화 설정
      userRepository.save(user);
    }
  }

  // UserDetailsService 구현
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
    if (userOpt.isEmpty()) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }
    User user = userOpt.get();
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .roles("USER") // 필요에 따라 역할 설정
        .build();
  }

  // 세션 인증 상태 확인
  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken);
  }

  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsernameAndIsActiveTrue(username);
  }

  public Optional<String> getUserProfileImageUrlByUsername(String username) {
    return userRepository.findByUsername(username).map(User::getProfilePictureUrl);
  }

  // UserService.java
  public void updateUser(Long id, UserUpdateRequestDTO updateRequest) {
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

    // Update fields if present
    if (updateRequest.getUsername() != null) {
      user.setUsername(updateRequest.getUsername());
    }
    if (updateRequest.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }
    if (updateRequest.getProfilePictureUrl() != null) {
      user.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
    }
    if (updateRequest.getBio() != null) {  // bio 추가
      user.setBio(updateRequest.getBio());
    }
    if (updateRequest.getEmail() != null) {  // email 추가
      user.setEmail(updateRequest.getEmail());
    }
    if (updateRequest.getVisibility() != null) {  // visibility 추가
      user.setVisibility(ProfileVisibility.valueOf(updateRequest.getVisibility()));
    }
    if (updateRequest.getWebsite() != null) {  // email 추가
      user.setWebsite(updateRequest.getWebsite());
    }
    if (updateRequest.getActive() != null) {  // visibility 추가
      user.setActive(updateRequest.getActive());
    }

    userRepository.save(user);
  }

  public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
  }

  public Set<User> getFriendsByUserId(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    return user.getFriends();
  }

  @Transactional
  public void addFriend(String senderUsername, String receiverUsername) {
    // UserService에서 username으로 유저를 조회
    User sender = userRepository.findByUsername(senderUsername)
        .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

    User receiver = userRepository.findByUsername(receiverUsername)
        .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));


    // sender의 friends에 receiver를 추가
    sender.getFriends().add(receiver);
    // receiver의 friends에 sender를 추가
    receiver.getFriends().add(sender);

    // 친구 관계를 user 테이블에만 저장하고, 각 유저의 friends 목록은 필요하지 않음
    userRepository.save(sender); // 친구 관계가 아닌 유저 정보만 저장
    userRepository.save(receiver); // 친구 관계가 아닌 유저 정보만 저장
  }
}