package org.example.boundaryback.like;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.comment.Comment;
import org.example.boundaryback.user.User;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  // 좋아요 여부
  private boolean liked = true;
}
