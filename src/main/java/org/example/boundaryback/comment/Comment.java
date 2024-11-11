package org.example.boundaryback.comment;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.post.Post;
import org.example.boundaryback.user.User;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  private String content;

  @Column(name = "is_active")
  private boolean isActive = true;

  // 좋아요를 누른 유저 목록
  @ManyToMany
  @JoinTable(
      name = "comment_likes",
      joinColumns = @JoinColumn(name = "comment_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> likes;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at")
  private Date createdAt = new Date();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at")
  private Date updatedAt = new Date();

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = new Date();
  }
}
