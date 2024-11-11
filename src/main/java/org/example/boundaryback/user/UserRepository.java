package org.example.boundaryback.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  // 활성화된 사용자만 조회 (Username으로)
  Optional<User> findByUsernameAndIsActiveTrue(String username);

  // 활성화된 사용자만 조회 (ID로)
  Optional<User> findByIdAndIsActiveTrue(Long id);

  // 모든 활성화된 사용자 조회
  List<User> findByIsActiveTrue();
}
