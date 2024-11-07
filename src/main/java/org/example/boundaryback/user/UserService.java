package org.example.boundaryback.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // 사용자 등록
  public void registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  // 로그인 검증
  public boolean verifyLogin(String username, String password) {
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      return false;
    }
    User user = userOpt.get();
    return passwordEncoder.matches(password, user.getPassword());
  }

  // ID로 유저 조회
  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  // 모든 유저 조회
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // 유저 삭제
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  // UserDetailsService 구현
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOpt = userRepository.findByUsername(username);
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
}
