package org.example.boundaryback.comment;

import org.example.boundaryback.post.Post;
import org.example.boundaryback.post.PostService;
import org.example.boundaryback.user.User;
import org.example.boundaryback.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

  private final CommentService commentService;
  private final PostService postService;
  private final UserService userService;

  @Autowired
  public CommentController(CommentService commentService, PostService postService, UserService userService) {
    this.commentService = commentService;
    this.postService = postService;
    this.userService = userService;
  }

// 댓글 생성
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<Map<String, Object>> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
    // 댓글 작성자 및 게시물 정보 설정
    User author = userService.findUserById(commentRequestDTO.getAuthorId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));
    Post post = postService.getPostById(commentRequestDTO.getPostId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

    Comment comment = new Comment();
    comment.setAuthor(author);
    comment.setPost(post);
    comment.setContent(commentRequestDTO.getContent());

    // 댓글 생성
    Comment createdComment = commentService.createComment(comment);

    // 활성 댓글 리스트 가져오기
    List<CommentDTO> activeComments = commentService.getCommentsByPost3(post).stream()
        .map(CommentDTO::new)
        .collect(Collectors.toList());

    // 활성 댓글 개수 가져오기
    long activeCommentsCount = commentService.countActiveCommentsByPost(post);

    // 결과 맵으로 반환
    Map<String, Object> response = new HashMap<>();
    response.put("comments", activeComments);
    response.put("activeCommentsCount", activeCommentsCount);

    return ResponseEntity.ok(response);
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
        .map(post -> ResponseEntity.ok(commentService.getCommentsByPost3(post)))
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
