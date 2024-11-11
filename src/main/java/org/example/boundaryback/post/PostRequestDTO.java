package org.example.boundaryback.post;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class PostRequestDTO {
  private Long authorId;
  private List<String> imageUrls;
  private String caption;
  private List<String> hashtags;
}
