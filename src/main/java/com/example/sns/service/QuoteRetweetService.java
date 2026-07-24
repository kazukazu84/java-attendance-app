package com.example.sns.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.entity.QuoteRetweetEntity;
import com.example.sns.repository.QuoteRetweetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuoteRetweetService {

    private final QuoteRetweetRepository quoteRetweetRepository;

    @Transactional
    public void createQuoteRetweet(Long postId, String userId, String comment) {
        QuoteRetweetEntity quote = new QuoteRetweetEntity();
        quote.setPostId(postId);
        quote.setUserId(userId);
        quote.setComment(comment);
        quoteRetweetRepository.save(quote);
    }
}