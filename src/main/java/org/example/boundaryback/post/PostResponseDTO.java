package org.example.boundaryback.post;

import lombok.Getter;
import lombok.Setter;
import org.example.boundaryback.comment.CommentDTO;

import java.util.List;

@Getter
@Setter
public class PostResponseDTO {

  private Long id;
  private String caption;
  private String author; // 게시물의 작성자
  private List<CommentDTO> comments; // 댓글 목록
  private long activeCommentsCount; // 활성 댓글 수
  private boolean isLikedByCurrentUser; // 현재 유저의 좋아요 상태
  private long likeCount; // 좋아요 수
  private List<String> imageUrls; // 게시물의 이미지 URL 목록
  private String createdAt; // 게시물 생성 시간

  // 생성자
  public PostResponseDTO(Post post, List<CommentDTO> comments, long activeCommentsCount, boolean isLikedByCurrentUser, long likeCount) {
    this.id = post.getId();
    this.caption = post.getCaption();
    this.author = post.getAuthor().getUsername();
    this.comments = comments;
    this.activeCommentsCount = activeCommentsCount;
    this.isLikedByCurrentUser = isLikedByCurrentUser;
    this.likeCount = likeCount;
    this.imageUrls = post.getImageUrls(); // 이미지 URLs 추가
    this.createdAt = post.getCreatedAt().toString(); // 생성 시간 추가
  }
}