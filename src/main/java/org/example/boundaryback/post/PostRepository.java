package org.example.boundaryback.post;

import org.example.boundaryback.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByAuthorAndIsActiveTrue(User author);

  List<Post> findByIsActiveTrue();

  List<Post> findByAuthorInAndIsActiveTrue(List<User> authors); // 친구 기준 가져오기용
}
