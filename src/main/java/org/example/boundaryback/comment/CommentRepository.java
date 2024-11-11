package org.example.boundaryback.comment;

import org.example.boundaryback.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByPostAndIsActiveTrue(Post post);
}
