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

  public Optional<Post> getPostById(Long id) {
    return postRepository.findById(id).filter(Post::isActive);
  }

  public List<Post> getPostsByUser(User user) {
    return postRepository.findByAuthorAndIsActiveTrue(user);
  }

  public List<Post> getActivePosts() {
    return postRepository.findByIsActiveTrue();
  }

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
