package org.example.boundaryback.user;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
  private String username;
  private String password;
  private String profilePictureUrl;
  private String bio;  // 추가
  private String email; // 추가
  private String visibility; // 추가
  private String website;
  private Boolean active;
}
