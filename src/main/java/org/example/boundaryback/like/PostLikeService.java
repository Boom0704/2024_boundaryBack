package org.example.boundaryback.like;

import lombok.RequiredArgsConstructor;
import org.example.boundaryback.post.Post;
import org.example.boundaryback.post.PostService;
import org.example.boundaryback.user.User;
import org.example.boundaryback.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostLikeService {

  private final PostLikeRepository postLikeRepository;
  private final PostService postService;
  private final UserService userService;

  // 좋아요 상태 변경
  public PostLikeResponseDTO changeLike(Long postId, Long userId) {
    // Post와 User 조회
    Post post = postService.getPostById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    // 이미 좋아요를 누른 상태인지 확인
    Optional<PostLike> postLikeOptional = postLikeRepository.findByPostAndUser(post, user);
    boolean isLiked = false;

    if (postLikeOptional.isPresent()) {
      PostLike postLike = postLikeOptional.get();
      // 좋아요 상태 반전
      if (postLike.isLiked()) {
        postLike.setLiked(false); // 좋아요 취소
        isLiked = false;
      } else {
        postLike.setLiked(true); // 좋아요 눌림
        isLiked = true;
      }
      postLikeRepository.save(postLike);
    } else {
      // 처음 좋아요를 눌렀을 때
      PostLike newPostLike = new PostLike(post, user, true);
      postLikeRepository.save(newPostLike);
      isLiked = true;
    }

    // 전체 좋아요 수 갱신
    int totalLikes = postLikeRepository.countByPostAndLikedTrue(post);

    return new PostLikeResponseDTO(totalLikes, isLiked);
  }

  // 특정 포스트에 대한 좋아요 상태와 전체 좋아요 수 반환
  public PostLikeResponseDTO getPostLikeStatus(Long postId, Long userId) {
    // Post와 User 조회
    Post post = postService.getPostById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    // 좋아요 상태 확인
    boolean isLiked = postLikeRepository.existsByPostIdAndUserAndLikedTrue(post.getId(), user);

    // 전체 좋아요 수 조회
    int totalLikes = postLikeRepository.countByPostAndLikedTrue(post);

    return new PostLikeResponseDTO(totalLikes, isLiked);
  }

  // 특정 포스트의 좋아요 수 반환
  public int getLikeCount(Post post) {
    return postLikeRepository.countByPostAndLikedTrue(post);
  }

  // 현재 유저가 특정 포스트에 좋아요를 눌렀는지 확인
  public boolean isUserLikedPost(Post post, User user) {
    return postLikeRepository.existsByPostIdAndUserAndLikedTrue(post.getId(), user);
  }
}
