package org.example.boundaryback.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boundaryback.comment.Comment;
import org.example.boundaryback.hashtag.Hashtag;
import org.example.boundaryback.user.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ElementCollection
  @Column(name = "image_urls")
  private List<String> imageUrls;

  private String caption;

  // 좋아요를 누른 유저 목록
  @ManyToMany
  @JoinTable(
      name = "post_likes",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> likes;

  @Column(name = "is_active")
  private boolean isActive = true;

  @ManyToMany
  @JoinTable(
      name = "post_hashtags",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "hashtag_id")
  )
  private Set<Hashtag> hashtags;

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

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Comment> comments;
}
