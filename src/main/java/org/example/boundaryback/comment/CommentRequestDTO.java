package org.example.boundaryback.comment;

public class CommentRequestDTO {
  private Long authorId;
  private Long postId;
  private String content;

  // Getters and setters
  public Long getAuthorId() { return authorId; }
  public void setAuthorId(Long authorId) { this.authorId = authorId; }

  public Long getPostId() { return postId; }
  public void setPostId(Long postId) { this.postId = postId; }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
}
