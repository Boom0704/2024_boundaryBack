package org.example.boundaryback.hashtag;

import org.example.boundaryback.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
  Optional<Hashtag> findByName(String name);

  // 포스트와 관련된 해시태그들 가져오기
  Set<Hashtag> findByPosts(Post post);
}
