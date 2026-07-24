package com.example.sns.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.entity.UserInfo;
import com.example.account.service.CustomUserDetails;
import com.example.sns.dto.MessageDto;
import com.example.sns.form.MessageForm;
import com.example.sns.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping({"/user/dm", "/admin/dm"})
    public String showDmPage(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestParam(required = false) String receiverId,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/dm", "/admin/dm"
        );
        if (redirectUrl != null) return redirectUrl;

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String basePath = isAdmin ? "/admin" : "/user";
        model.addAttribute("basePath", basePath);

        String currentUserId = loginUser.getUsername();
        List<UserInfo> users = messageService.getOtherUsers(currentUserId);

        List<MessageDto> messages = new ArrayList<>();
        if (receiverId != null && !receiverId.isEmpty()) {
            messages = messageService.getMessages(currentUserId, receiverId);
        }

        MessageForm messageForm = new MessageForm();
        messageForm.setReceiverId(receiverId);

        model.addAttribute("users", users);
        model.addAttribute("activeReceiverId", receiverId);
        model.addAttribute("messages", messages);
        model.addAttribute("messageForm", messageForm);

        return "dm";
    }

    @PostMapping({"/user/dm/send", "/admin/dm/send"})
    public String sendMessage(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            HttpServletRequest request,
            @ModelAttribute MessageForm form) {

        if (loginUser == null) return "redirect:/login";

        String redirectUrl = checkAndRedirect(
                loginUser, request,
                "/user/dm/send", "/admin/dm/send"
        );
        if (redirectUrl != null) return redirectUrl;

        messageService.sendMessage(loginUser.getUsername(), form.getReceiverId(), form.getContent());

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return "redirect:" + (isAdmin ? "/admin/dm?receiverId=" : "/user/dm?receiverId=") + form.getReceiverId();
    }

    private String checkAndRedirect(CustomUserDetails loginUser, HttpServletRequest request,
                                    String userPath, String adminPath) {

        boolean isAdmin = loginUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String requestUri = request.getRequestURI();

        if (isAdmin && requestUri.endsWith(userPath)) {
            return "redirect:" + adminPath;
        }

        if (!isAdmin && requestUri.endsWith(adminPath)) {
            return "redirect:" + userPath;
        }

        return null;
    }
}
