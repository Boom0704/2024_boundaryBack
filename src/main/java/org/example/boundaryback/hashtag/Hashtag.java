package org.example.boundaryback.hashtag;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.post.Post;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  @ManyToMany(mappedBy = "hashtags")
  private Set<Post> posts;

  public Hashtag(String name) {
    this.name = name;
  }
}
