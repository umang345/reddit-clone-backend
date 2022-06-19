package com.umang345.redditclonebackend.service;

import com.umang345.redditclonebackend.dto.SubredditDto;
import com.umang345.redditclonebackend.exceptions.SpringRedditException;
import com.umang345.redditclonebackend.mapper.SubredditMapper;
import com.umang345.redditclonebackend.model.Subreddit;
import com.umang345.redditclonebackend.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService
{
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto)
    {
        System.out.println("Subredit Service.save()");
        Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll()
    {
         return subredditRepository.findAll()
                 .stream()
                 .map(subredditMapper::mapSubredditToDto)
                 .collect(toList());
    }

    @Transactional
    public SubredditDto getSubredditById(Long id)
    {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No Subreddit found with id : "+id));

        return subredditMapper.mapSubredditToDto(subreddit);
    }

//    private SubredditDto mapToDto(Subreddit subreddit)
//    {
//         return SubredditDto.builder().name(subreddit.getName())
//                 .id(subreddit.getId())
//                 .numberOfPosts(subreddit.getPosts().size())
//                 .build();
//    }
//
//    private Subreddit mapSubredditDto(SubredditDto subredditDto)
//    {
//        return Subreddit.builder().name(subredditDto.getName())
//                .description(subredditDto.getDescription())
//                .build();
//    }

}
