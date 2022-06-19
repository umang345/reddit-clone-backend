package com.umang345.redditclonebackend.repository;

import com.umang345.redditclonebackend.model.Comment;
import com.umang345.redditclonebackend.model.Post;
import com.umang345.redditclonebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
