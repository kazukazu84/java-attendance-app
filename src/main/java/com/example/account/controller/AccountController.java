package com.example.account.controller;

import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.UserInfo;
import com.example.account.service.AccountService;

@Controller
public class AccountController {

	@Autowired 
	private AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}
	/**
	 * アカウント登録画面の表示
	 */
	@GetMapping("/admin/register")
	public String registerView(Model model) { 
		// 💡 フォームオブジェクトを生成
		UserRegisterForm form = new UserRegisterForm();

		// 💡 在籍ステータスのデフォルト値を「1 (有効)」に設定する
		form.setIsActive(1);

		model.addAttribute("userRegisterForm", form);
		model.addAttribute("wages", accountService.getAllWages());
		// 💡 新規登録画面であることを明示するフラグを渡す
		model.addAttribute("isNew", true); 

		return "account/admin/register"; 
	}

	/**
	 * 新規登録処理
	 */
	@PostMapping("/admin/register")
	public String register(@Valid @ModelAttribute("userRegisterForm") UserRegisterForm form, 
			BindingResult result, 
			//,@AuthenticationPrincipal UserDetails userDetails  
			Model model
			) {

		// 1. パスワード等の単体入力チェック（アノテーションや手動チェック）
		if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
			result.rejectValue("password", "error.password", "パスワードは必須入力です。");
		}

		// 💡 【変更点】ここにあった controller 側の if (accountService.existsByUserId(...)) は丸ごと削除！
		// 重複チェックはサービスへ完全委譲します。

		// 事前入力エラーがあれば即座に画面差し戻し
		if (result.hasErrors()) {
			model.addAttribute("wages", accountService.getAllWages());
			model.addAttribute("isNew", true); 
			return "account/admin/register";
		}

		// 2. サービス実行（重複チェックは Service 内で実行され、重複時は例外が飛んでくる）
		try {
			accountService.registerAccount(form
					//, userDetails.getUsername()
					);  
		} catch (IllegalArgumentException e) {
			// ⚡ Service から投げられた「重複例外」をキャッチして画面にバインド！
			result.rejectValue("userId", "error.userId", e.getMessage());

			// エラー時用にモデルを再設定して差し戻し
			model.addAttribute("wages", accountService.getAllWages());
			model.addAttribute("isNew", true); 
			return "account/admin/register";
		}

		return "redirect:/admin/UserManagement";
	}

	/**
	 * アカウント編集画面の表示
	 */
	@GetMapping("/admin/edit/{id}")
	public String showEditForm(@PathVariable("id") String id, 
			@AuthenticationPrincipal UserDetails userDetails,
			Model model, RedirectAttributes redirectAttributes) {
		
		Optional<UserInfo> userOpt = accountService.findUserById(id);

		if (id.equals(userDetails.getUsername())) {
            model.addAttribute("errorMessage", "現在ログイン中の自分自身を編集・無効化することはできませんぞい！");
            return "account/error-denied";
        }

		if (userOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "指定されたユーザーが見つかりません。");
			return "redirect:/admin/UserManagement";
		}

		UserRegisterForm form = accountService.getEditForm(userOpt.get());

        model.addAttribute("userRegisterForm", form);
        model.addAttribute("wages", accountService.getAllWages());
		// 💡 編集画面であることを明示するフラグを渡す
		model.addAttribute("isNew", false); 

		return "account/admin/register";
	}

	/**
	 * アカウント更新処理
	 */
	@PostMapping("/admin/update")
	public String updateAccount(@Valid @ModelAttribute("userRegisterForm") UserRegisterForm form,
			BindingResult result,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (form.getUserId().equals(userDetails.getUsername())) {
            model.addAttribute("errorMessage", "現在ログイン中の自分自身を編集・無効化することはできませんぞい！");
            return "account/error-denied";
        }

		if (form.getPassword() != null && form.getPassword().trim().isEmpty()) {
			result.rejectValue("password", "error.password", "パスワードを入力してください。");
		}

		if (result.hasErrors()) {
			model.addAttribute("wages", accountService.getAllWages());

			// 💡 エラー差し戻し時も「編集画面」であることを維持する
			model.addAttribute("isNew", false); 

			return "account/admin/register";
		}

		boolean isUpdated = accountService.updateAccount(form);

		if (!isUpdated) {
			redirectAttributes.addFlashAttribute("errorMessage", "更新対象のユーザーが存在しません。");
			return "redirect:/admin/UserManagement";
		}

		redirectAttributes.addFlashAttribute("successMessage", "アカウント情報を更新しました。");
		return "redirect:/admin/UserManagement";
	}
}