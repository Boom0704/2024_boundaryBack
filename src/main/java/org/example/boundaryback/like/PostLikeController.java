package org.example.boundaryback.like;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostLikeController {

  private final PostLikeService postLikeService;

  @PostMapping("/{postId}/like")
  public PostLikeResponseDTO changeLike(
      @PathVariable Long postId,
      @RequestParam Long userId
  ) {
    return postLikeService.changeLike(postId, userId);
  }

  @GetMapping("/{postId}/like")
  public PostLikeResponseDTO getPostLikeStatus(
      @PathVariable Long postId,
      @RequestParam Long userId
  ) {
    return postLikeService.getPostLikeStatus(postId, userId);
  }
}
