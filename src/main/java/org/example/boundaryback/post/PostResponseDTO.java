package org.example.boundaryback.post;

import lombok.Getter;
import lombok.Setter;
import org.example.boundaryback.comment.CommentDTO;
import org.example.boundaryback.hashtag.Hashtag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
  private List<String> hashtags; // 해시태그 목록

  // 생성자
  public PostResponseDTO(Post post, List<CommentDTO> comments, long activeCommentsCount, boolean isLikedByCurrentUser, long likeCount, Set<Hashtag> hashtags) {
    this.id = post.getId();
    this.caption = post.getCaption();
    this.author = post.getAuthor().getUsername();  // 게시물 작성자의 username을 가져옵니다.
    this.comments = comments;  // 댓글 목록을 전달받아 설정
    this.activeCommentsCount = activeCommentsCount;  // 활성 댓글 수 설정
    this.isLikedByCurrentUser = isLikedByCurrentUser;  // 현재 유저의 좋아요 상태 설정
    this.likeCount = likeCount;  // 좋아요 수 설정
    this.imageUrls = post.getImageUrls();  // 게시물의 이미지 URL 목록 설정
    this.createdAt = post.getCreatedAt().toString();  // 게시물 생성 시간 설정 (예: "2024-11-12T17:00:00")
    this.hashtags = hashtags.stream()
        .map(Hashtag::getName)  // 해시태그 이름만 추출하여 리스트로 변환
        .collect(Collectors.toList());
  }
}
