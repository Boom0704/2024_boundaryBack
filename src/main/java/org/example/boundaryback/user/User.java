package org.example.boundaryback.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.comment.Comment;
import org.example.boundaryback.post.Post;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  private String profilePictureUrl;

  private String bio;

  private String website;

  @Enumerated(EnumType.STRING)
  private ProfileVisibility visibility;

  private boolean isActive = true;

  @Temporal(TemporalType.DATE)
  private Date birthday;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt = new Date();

  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt = new Date();

  // 친구 관계 설정
  @ManyToMany
  @JoinTable(
      name = "user_friends",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "friend_id")
  )
  private Set<User> friends;

  // 좋아요 관계 설정
  @ManyToMany(mappedBy = "likes")
  private Set<Post> likedPosts;

  @ManyToMany(mappedBy = "likes")
  private Set<Comment> likedComments;

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = new Date();
  }
}
