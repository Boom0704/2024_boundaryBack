package org.example.boundaryback.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.boundaryback.hashtag.Hashtag;
import org.example.boundaryback.hashtag.HashtagService;
import org.example.boundaryback.user.User;
import org.example.boundaryback.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final UserService userService;
  private final HashtagService hashtagService;

  @Autowired
  public PostController(PostService postService, UserService userService, HashtagService hashtagService, ObjectMapper objectMapper) {
    this.postService = postService;
    this.userService = userService;
    this.hashtagService = hashtagService;
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


  // 단일 조회
  @PreAuthorize("permitAll()")
  @GetMapping("/{id}")
  public ResponseEntity<Post> getPostById(@PathVariable Long id) {
    return postService.getPostById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // 유저의 게시물 조회
  @PreAuthorize("permitAll()")
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
    return userService.getUserById(userId)
        .map(user -> ResponseEntity.ok(postService.getPostsByUser(user)))
        .orElse(ResponseEntity.notFound().build());
  }

  // 전체 활성화된 게시물 조회
  @PreAuthorize("permitAll()")
  @GetMapping("/active")
  public ResponseEntity<List<Post>> getActivePosts() {
    return ResponseEntity.ok(postService.getActivePosts());
  }

  // 친구의 게시물 조회 (빈 리스트 반환)
  @PreAuthorize("permitAll()")
  @GetMapping("/friends")
  public ResponseEntity<List<Post>> getPostsByFriends() {
    return ResponseEntity.ok(List.of()); // 빈 데이터 반환
  }

  // 수정
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

  // 삭제 (비활성화)
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deactivatePost(@PathVariable Long id) {
    postService.deactivatePost(id);
    return ResponseEntity.ok("Post deactivated successfully!");
  }
}
