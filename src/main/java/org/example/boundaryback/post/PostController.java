package org.example.boundaryback.post;

import org.example.boundaryback.comment.CommentDTO;
import org.example.boundaryback.comment.CommentService;
import org.example.boundaryback.hashtag.Hashtag;
import org.example.boundaryback.hashtag.HashtagService;
import org.example.boundaryback.like.PostLikeService;
import org.example.boundaryback.user.User;
import org.example.boundaryback.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final UserService userService;
  private final HashtagService hashtagService;
  private final CommentService commentService;
  private final PostLikeService postLikeService;

  @Autowired
  public PostController(PostService postService, UserService userService, HashtagService hashtagService,
                        CommentService commentService, PostLikeService postLikeService) {
    this.postService = postService;
    this.userService = userService;
    this.hashtagService = hashtagService;
    this.commentService = commentService;
    this.postLikeService = postLikeService;
  }

  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    org.springframework.security.core.userdetails.User principal =
        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

    // 현재 인증된 사용자의 id로 실제 User를 가져옵니다.
    return userService.getUserByUsername(principal.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<Post> createPost(@RequestBody PostRequestDTO postRequest) {
    // Post 객체 생성 및 필드 설정
    Post post = new Post();
    post.setImageUrls(postRequest.getImageUrls());
    post.setCaption(postRequest.getCaption());

    User author = userService.getUserById(postRequest.getAuthorId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));
    post.setAuthor(author);

    Set<Hashtag> hashtagEntities = hashtagService.findOrCreateHashtags(postRequest.getHashtags());
    post.setHashtags(hashtagEntities);

    // 게시물 저장 후 응답 반환
    Post savedPost = postService.createPost(post);
    return ResponseEntity.ok(savedPost);
  }

  // 단일 게시물 조회 (댓글 포함, 좋아요 상태 및 수 포함)
  @PreAuthorize("permitAll()")
  @GetMapping("/{id}")
  public ResponseEntity<PostResponseDTO> getPostByIdWithComments(@PathVariable Long id) {
    return postService.getPostById(id)
        .map(post -> {
          User currentUser = getCurrentUser();  // 현재 로그인된 사용자 정보 가져오기

          // 댓글 목록 가져오기
          List<CommentDTO> comments = commentService.getCommentsByPost(post)
              .stream()
              .map(CommentDTO::new)
              .collect(Collectors.toList());

          // 활성 댓글 개수 가져오기
          long activeCommentsCount = commentService.countActiveCommentsByPost(post);

          // 해당 포스트에 대한 좋아요 수와 로그인된 유저의 좋아요 상태 가져오기
          long likeCount = postLikeService.getLikeCount(post);
          boolean isLiked = postLikeService.isUserLikedPost(post, currentUser);

          // 해당 포스트에 연결된 해시태그 가져오기
          Set<Hashtag> hashtags = hashtagService.getHashtagsForPost(post);

          // PostResponseDTO에 댓글 목록, 활성 댓글 개수, 좋아요 수, 유저의 좋아요 상태 포함하여 반환
          PostResponseDTO postResponseDTO = new PostResponseDTO(post, comments, activeCommentsCount, isLiked, likeCount, hashtags);
          return ResponseEntity.ok(postResponseDTO);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/active")
  public ResponseEntity<List<PostResponseDTO>> getActivePostsWithComments() {
    // 여기서 activePosts를 가져오는 로직을 그대로 유지합니다
    List<PostResponseDTO> activePostsWithComments = postService.getActivePosts()
        .stream()
        .map(post -> {
          // 각 활성 게시물에 대해 댓글 목록과 활성 댓글 개수 가져오기
          List<CommentDTO> comments = commentService.getCommentsByPost3(post).stream()
              .map(CommentDTO::new)
              .collect(Collectors.toList());
          long activeCommentsCount = commentService.countActiveCommentsByPost(post);

          // 해당 포스트에 대한 좋아요 수와 로그인된 유저의 좋아요 상태 가져오기
          long likeCount = postLikeService.getLikeCount(post);
          User currentUser = getCurrentUser(); // 로그인한 사용자 정보
          boolean isLiked = postLikeService.isUserLikedPost(post, currentUser);

          // 해당 포스트에 연결된 해시태그 가져오기
          Set<Hashtag> hashtags = hashtagService.getHashtagsForPost(post);

          return new PostResponseDTO(post, comments, activeCommentsCount, isLiked, likeCount, hashtags);
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(activePostsWithComments);
  }

  // 게시물 수정
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
    return postService.getPostById(id)
        .map(existingPost -> {
          post.setId(id);
          return ResponseEntity.ok(postService.updatePost(post));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  // 게시물 삭제 (비활성화)
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deactivatePost(@PathVariable Long id) {
    postService.deactivatePost(id);
    return ResponseEntity.ok("Post deactivated successfully!");
  }
}
