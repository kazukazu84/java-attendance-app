package com.example.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.entity.UserInfo;
import com.example.account.service.AccountService;

@Controller
public class UserManagementController {
	@Autowired private AccountService accountService;

	// 💡 コンストラクタ注入（Repositoryへの直接依存を排除）
    public UserManagementController(AccountService accountService) {
        this.accountService = accountService;
    }
	
	@GetMapping("/admin/UserManagement")
	public String usermanagementView(Model model) { 
		// Service 経由で全件取得
        List<UserInfo> userList = accountService.searchUsers("", "");
		// 2. HTML側の th:each="user : ${users}" にマッピングできるように "users" という名前でセット
		model.addAttribute("users", userList);
		// 3. 表示するHTMLテンプレートのパスを返す
		return "account/admin/UserManagement"; 
	}

	@GetMapping("/admin/users/search")
	public String search(
			@RequestParam("keyword") String keyword,
			@RequestParam("type") String type,
			Model model) {

		List<UserInfo> searchResults = accountService.searchUsers(keyword, type); // 💡 UserInfo に変更

		model.addAttribute("users", searchResults);
		return "account/admin/UserManagement";
	}

	@PostMapping("/admin/users/batch-deactivate")
	public String batchDeactivate(@RequestParam(value = "userIds", required = false) List<String> userIds
			,@AuthenticationPrincipal UserDetails userDetails
			) {

		// チェックなしで送信された場合は何もせずリダイレクト
        if (userIds == null || userIds.isEmpty()) {
            return "redirect:/admin/UserManagement";
        }
		
     // 💡 @AuthenticationPrincipal でログインユーザーIDを取得
        String loginUserId = userDetails.getUsername();

        // 🛡️ 【鉄壁の防壁】無効化リストの中に自分がいたら即座に中止！
        if (userIds.contains(loginUserId)) {
        	return "account/error-denied";
        }
		
        accountService.deactivateUsers(userIds);

		// 🔄 処理が終わったら、検索が解除された初期の一覧画面（URL）へリダイレクト！
		return "redirect:/admin/UserManagement"; 
	}
}
