package com.example.sns.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.dto.QuoteRetweetDto;
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
    
    @Transactional(readOnly = true)
    public List<QuoteRetweetDto> getQuotes(Long postId) {
        return quoteRetweetRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(q -> {
                    QuoteRetweetDto dto = new QuoteRetweetDto();
                    dto.setQuoteId(q.getQuoteId());
                    dto.setPostId(q.getPostId());
                    dto.setUserId(q.getUserId());
                    dto.setUserName(q.getUser().getUserName());
                    dto.setComment(q.getComment());
                    dto.setCreatedAt(q.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}