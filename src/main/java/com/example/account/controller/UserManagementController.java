package com.example.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;
import com.example.account.service.AccountService;

@Controller
public class UserManagementController {
	@Autowired private AccountService accountService;
	@Autowired private UserInfoRepository userInfoRepository;

	@GetMapping("/admin/UserManagement")
	public String usermanagementView(Model model) { 
		// 1. DBからすべてのユーザー情報を取得
		List<UserInfo> userList = userInfoRepository.findAll();
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
	public String batchDeactivate(@RequestParam(value = "userIds", required = false) List<String> userIds) {

		// 💡 オブジェクト（SecurityContext）からログイン中のユーザーIDを抽出！
        String loginUserId = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

        // 🛡️ 【鉄壁の防壁】無効化リストの中に自分がいたら即座に中止！
        if (userIds.contains(loginUserId)) {
        	return "account/error-denied";
        }
		
		// 💡 何もチェックされずに届いた場合の安全ガード
		if (userIds != null && !userIds.isEmpty()) {
			// 次のステップで作る「サービス」の無効化メソッドを呼び出す
			accountService.deactivateUsers(userIds);
		}

		// 🔄 処理が終わったら、検索が解除された初期の一覧画面（URL）へリダイレクト！
		return "redirect:/admin/UserManagement"; 
	}
}
