package org.example.boundaryback.like;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.post.Post;
import org.example.boundaryback.user.User;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  // 좋아요 여부
  private boolean liked = true;

  // 생성자 추가: Post, User, liked 필드를 받는 생성자
  public PostLike(Post post, User user, boolean liked) {
    this.post = post;
    this.user = user;
    this.liked = liked;
  }
}
