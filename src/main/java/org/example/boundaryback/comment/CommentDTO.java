package org.example.boundaryback.comment;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentDTO {
  private Long id;
  private String content;
  private String author;
  private Date createdAt;

  // Comment 객체를 받아서 초기화하는 생성자 추가
  public CommentDTO(Comment comment) {
    this.id = comment.getId();
    this.content = comment.getContent();
    this.author = comment.getAuthor().getUsername(); // Author 정보에서 Username을 가져옴
    this.createdAt = comment.getCreatedAt();
  }
}
