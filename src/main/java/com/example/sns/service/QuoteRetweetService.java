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

    // ▼ 引用RT作成（元投稿 or 引用RTへの返信）
    @Transactional
    public void createQuoteRetweet(Long postId, String userId, String comment, Long parentQuoteId) {

        QuoteRetweetEntity quote = new QuoteRetweetEntity();
        quote.setPostId(postId);
        quote.setUserId(userId);
        quote.setComment(comment);
        quote.setParentQuoteId(parentQuoteId);

        quoteRetweetRepository.save(quote);
    }

    // ▼ 元投稿への引用RT（parentQuoteId = null）
    @Transactional(readOnly = true)
    public List<QuoteRetweetDto> getRootQuotes(Long postId) {
        return quoteRetweetRepository
                .findByPostIdAndParentQuoteIdIsNullOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ▼ 引用RTへの返信（parentQuoteId = quoteId）
    @Transactional(readOnly = true)
    public List<QuoteRetweetDto> getChildQuotes(Long parentQuoteId) {
        return quoteRetweetRepository
                .findByParentQuoteIdOrderByCreatedAtAsc(parentQuoteId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ▼ 親引用RTを単体取得（引用RTページで必要）
    @Transactional(readOnly = true)
    public QuoteRetweetDto getQuoteById(Long quoteId) {
        return quoteRetweetRepository.findById(quoteId)
                .map(this::toDto)
                .orElse(null);
    }

    // ▼ DTO変換（共通化）
    private QuoteRetweetDto toDto(QuoteRetweetEntity q) {
        QuoteRetweetDto dto = new QuoteRetweetDto();
        dto.setQuoteId(q.getQuoteId());
        dto.setPostId(q.getPostId());
        dto.setUserId(q.getUserId());
        dto.setUserName(q.getUser().getUserName());
        dto.setComment(q.getComment());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setParentQuoteId(q.getParentQuoteId());
        return dto;
    }
}
