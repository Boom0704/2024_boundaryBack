package org.example.boundaryback.post;

import org.example.boundaryback.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  @EntityGraph(attributePaths = {"comments"})
  Optional<Post> findById(Long id);

  @EntityGraph(attributePaths = {"comments"})
  List<Post> findByAuthorAndIsActiveTrue(User author);

  @EntityGraph(attributePaths = {"comments"})
  List<Post> findByIsActiveTrue();

  @EntityGraph(attributePaths = {"comments"})
  List<Post> findByAuthorInAndIsActiveTrue(List<User> authors);
}
