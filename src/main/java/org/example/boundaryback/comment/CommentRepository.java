package org.example.boundaryback.comment;

import org.example.boundaryback.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.isActive = true ORDER BY c.createdAt DESC")
  List<Comment> findTop3ByPostAndIsActiveTrueOrderByCreatedAtDesc(@Param("post") Post post, Pageable pageable);

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post AND c.isActive = true")
  long countActiveCommentsByPost(@Param("post") Post post);
}