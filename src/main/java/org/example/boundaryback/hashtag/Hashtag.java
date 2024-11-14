package org.example.boundaryback.hashtag;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.post.Post;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Hashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  // ManyToMany 관계 설정, mappedBy를 통해 양방향 관계 설정
  @ManyToMany(mappedBy = "hashtags", fetch = FetchType.LAZY)
  private Set<Post> posts = new HashSet<>();

  // 생성자: 이름 필드만 필요로 할 때 사용
  public Hashtag(String name) {
    this.name = name;
  }

  // toString을 id와 name만 포함하도록 설정해 순환 참조 문제 방지
  @Override
  public String toString() {
    return "Hashtag{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
