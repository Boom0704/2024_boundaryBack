package org.example.boundaryback.comment;

import org.example.boundaryback.post.Post;
import org.example.boundaryback.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostService postService;

  @Autowired
  public CommentService(CommentRepository commentRepository, PostService postService) {
    this.commentRepository = commentRepository;
    this.postService = postService;
  }

  public Comment createComment(Comment comment) {
    return commentRepository.save(comment);
  }

  public Optional<Comment> getCommentById(Long id) {
    return commentRepository.findById(id).filter(Comment::isActive);
  }

  public List<Comment> getCommentsByPost(Post post) {
    return commentRepository.findByPostAndIsActiveTrue(post);
  }

  public Comment updateComment(Comment comment) {
    return commentRepository.save(comment);
  }

  public void deactivateComment(Long id) {
    commentRepository.findById(id).ifPresent(comment -> {
      comment.setActive(false);
      commentRepository.save(comment);
    });
  }
}
