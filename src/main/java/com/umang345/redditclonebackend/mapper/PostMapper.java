package com.umang345.redditclonebackend.mapper;

import com.umang345.redditclonebackend.dto.PostRequest;
import com.umang345.redditclonebackend.dto.PostResponse;
import com.umang345.redditclonebackend.model.Post;
import com.umang345.redditclonebackend.model.Subreddit;
import com.umang345.redditclonebackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper
{
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    PostResponse mapToDto(Post post);
}
