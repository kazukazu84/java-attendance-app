package com.example.sns.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;
import com.example.sns.dto.MessageDto;
import com.example.sns.entity.MessageEntity;
import com.example.sns.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserInfoRepository userInfoRepository;

    @Transactional(readOnly = true)
    public List<UserInfo> getOtherUsers(String currentUserId) {
        return userInfoRepository.findAll().stream()
                .filter(user -> !user.getUserId().equals(currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessages(String currentUserId, String targetUserId) {

        List<MessageEntity> messages = messageRepository.findChatHistory(currentUserId, targetUserId);

        return messages.stream().map(m -> {

            String senderName = userInfoRepository.findById(m.getSenderId())
                    .map(UserInfo::getUserName)
                    .orElse("Unknown");

            String receiverName = userInfoRepository.findById(m.getReceiverId())
                    .map(UserInfo::getUserName)
                    .orElse("Unknown");

            return MessageDto.builder()
                    .messageId(m.getMessageId())
                    .senderId(m.getSenderId())
                    .senderName(senderName)
                    .receiverId(m.getReceiverId())
                    .receiverName(receiverName)
                    .content(m.getContent())
                    .createdAt(m.getCreatedAt())
                    .isMine(m.getSenderId().equals(currentUserId))
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void sendMessage(String senderId, String receiverId, String content) {
        MessageEntity message = new MessageEntity();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        messageRepository.save(message);
    }
}
