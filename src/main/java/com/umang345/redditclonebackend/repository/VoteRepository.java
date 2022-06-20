package com.umang345.redditclonebackend.repository;

import com.umang345.redditclonebackend.model.Post;
import com.umang345.redditclonebackend.model.User;
import com.umang345.redditclonebackend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
