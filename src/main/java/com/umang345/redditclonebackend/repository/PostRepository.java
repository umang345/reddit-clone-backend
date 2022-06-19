package com.umang345.redditclonebackend.repository;

import com.umang345.redditclonebackend.model.Post;
import com.umang345.redditclonebackend.model.Subreddit;
import com.umang345.redditclonebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
