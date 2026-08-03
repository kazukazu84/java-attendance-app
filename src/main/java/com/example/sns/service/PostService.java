package com.example.sns.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.dto.PostDto;
import com.example.sns.entity.PostEntity;
import com.example.sns.repository.LikeRepository;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.QuoteRetweetRepository;
import com.example.sns.repository.RetweetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final RetweetRepository retweetRepository;
    private final QuoteRetweetRepository quoteRetweetRepository;

    @Transactional(readOnly = true)
    public List<PostDto> getTimeline(String currentUserId) {

        List<PostEntity> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream().map(post -> {

            long likeCount = likeRepository.countByPostId(post.getPostId());
            long retweetCount = retweetRepository.countByPostId(post.getPostId());
            long quoteCount = quoteRetweetRepository.countByPostId(post.getPostId());

            boolean liked = likeRepository.existsByPostIdAndUserId(post.getPostId(), currentUserId);
            boolean retweeted = retweetRepository.existsByPostIdAndUserId(post.getPostId(), currentUserId);
            boolean quoted = quoteRetweetRepository.existsByPostIdAndUserId(post.getPostId(), currentUserId);

            String userName = post.getUser().getUserName();

            return PostDto.builder()
                    .postId(post.getPostId())
                    .userId(post.getUserId())
                    .userName(userName)
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .likeCount(likeCount)
                    .retweetCount(retweetCount)
                    .quoteCount(quoteCount)
                    .likedByCurrentUser(liked)
                    .retweetedByCurrentUser(retweeted)
                    .quotedByCurrentUser(quoted)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createPost(String userId, String content) {
        PostEntity post = new PostEntity();
        post.setUserId(userId);
        post.setContent(content);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostEntity getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません。ID: " + postId));
    }
}
