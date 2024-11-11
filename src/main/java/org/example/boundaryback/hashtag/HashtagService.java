package org.example.boundaryback.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HashtagService {

  private final HashtagRepository hashtagRepository;

  @Autowired
  public HashtagService(HashtagRepository hashtagRepository) {
    this.hashtagRepository = hashtagRepository;
  }

  public Set<Hashtag> findOrCreateHashtags(List<String> names) {
    Set<Hashtag> hashtags = new HashSet<>();
    for (String name : names) {
      Hashtag hashtag = hashtagRepository.findByName(name)
          .orElseGet(() -> hashtagRepository.save(new Hashtag(name)));
      hashtags.add(hashtag);
    }
    return hashtags;
  }
}
