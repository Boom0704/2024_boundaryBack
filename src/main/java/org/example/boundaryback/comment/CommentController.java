package org.example.boundaryback.comment;

import org.example.boundaryback.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

  private final CommentService commentService;
  private final PostService postService;

  @Autowired
  public CommentController(CommentService commentService, PostService postService) {
    this.commentService = commentService;
    this.postService = postService;
  }

  // 댓글 생성
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
    return ResponseEntity.ok(commentService.createComment(comment));
  }

  // 단일 댓글 조회
  @PreAuthorize("permitAll()")
  @GetMapping("/{id}")
  public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
    return commentService.getCommentById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // 특정 게시글의 모든 댓글 조회
  @PreAuthorize("permitAll()")
  @GetMapping("/post/{postId}")
  public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
    return postService.getPostById(postId)
        .map(post -> ResponseEntity.ok(commentService.getCommentsByPost(post)))
        .orElse(ResponseEntity.notFound().build());
  }

  // 댓글 수정
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
    return commentService.getCommentById(id)
        .map(existingComment -> {
          comment.setId(id);
          return ResponseEntity.ok(commentService.updateComment(comment));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  // 댓글 삭제 (비활성화)
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deactivateComment(@PathVariable Long id) {
    commentService.deactivateComment(id);
    return ResponseEntity.ok("Comment deactivated successfully!");
  }
}
