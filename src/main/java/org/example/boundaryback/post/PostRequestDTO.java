package org.example.boundaryback.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostRequestDTO {
  private Long authorId;          // 작성자 ID
  private String caption;          // 게시물 설명
  private List<String> imageUrls;  // 이미지 URL 리스트
  private List<String> hashtags;   // 해시태그 리스트
}
