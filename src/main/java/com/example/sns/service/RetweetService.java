package com.example.sns.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.entity.RetweetEntity;
import com.example.sns.repository.RetweetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetweetService {

    private final RetweetRepository retweetRepository;

    @Transactional
    public void toggleRetweet(Long postId, String userId) {
        Optional<RetweetEntity> existing = retweetRepository.findByPostIdAndUserId(postId, userId);
        if (existing.isPresent()) {
            retweetRepository.delete(existing.get());
        } else {
            RetweetEntity retweet = new RetweetEntity();
            retweet.setPostId(postId);
            retweet.setUserId(userId);
            retweetRepository.save(retweet);
        }
    }
}