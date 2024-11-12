package org.example.boundaryback.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostLikeResponseDTO {

  private int totalLikes;
  private boolean isLiked;

}
