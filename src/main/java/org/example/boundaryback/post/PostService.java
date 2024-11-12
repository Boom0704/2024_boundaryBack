package org.example.boundaryback.post;

import org.example.boundaryback.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

  private final PostRepository postRepository;

  @Autowired
  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  public Post createPost(Post post) {
    return postRepository.save(post);
  }

  // 연관된 comments 포함하여 ID로 조회
  public Optional<Post> getPostById(Long id) {
    return postRepository.findById(id).filter(Post::isActive);
  }

  // 연관된 comments 포함하여 특정 유저의 활성화된 게시물 조회
  public List<Post> getPostsByUser(User user) {
    return postRepository.findByAuthorAndIsActiveTrue(user);
  }

  // 연관된 comments 포함하여 모든 활성화된 게시물 조회
  public List<Post> getActivePosts() {
    return postRepository.findByIsActiveTrue();
  }

  // 연관된 comments 포함하여 친구들의 활성화된 게시물 조회
  public List<Post> getPostsByFriends(List<User> friends) {
    return postRepository.findByAuthorInAndIsActiveTrue(friends);
  }

  public Post updatePost(Post post) {
    return postRepository.save(post);
  }

  public void deactivatePost(Long id) {
    postRepository.findById(id).ifPresent(post -> {
      post.setActive(false);
      postRepository.save(post);
    });
  }
}
