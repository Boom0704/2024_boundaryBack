package org.example.boundaryback.like;

import org.example.boundaryback.post.Post;
import org.example.boundaryback.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  // Post와 User 객체를 기준으로 좋아요 정보를 찾는 메소드
  Optional<PostLike> findByPostAndUser(Post post, User user);

  // 특정 포스트에 대해 좋아요를 누른 수를 반환하는 메소드
  int countByPostAndLikedTrue(Post post);

  // 특정 포스트에 대해 User가 좋아요를 눌렀는지 확인하는 메소드
  boolean existsByPostIdAndUserAndLikedTrue(Long postId, User user);
}
