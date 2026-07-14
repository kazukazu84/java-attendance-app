package com.example.account.controller;

import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.service.AccountService;

@Controller
public class AccountController {
    
    @Autowired 
    private AccountService accountService;
    
    /**
     * アカウント登録画面の表示
     * (Thymeleafのth:objectエラー防止のため、空のFormオブジェクトをモデルに格納します)
     */
    @GetMapping("/admin/register")
    public String registerView(Model model) { 
        model.addAttribute("userRegisterForm", new UserRegisterForm());
        return "account/admin/register"; 
    }
    
    

    /**
     * 新規登録処理
     */
    @PostMapping("/admin/register")
    public String register(@ModelAttribute("userRegisterForm") UserRegisterForm form) {
        accountService.registerAccount(
            form.getUserId(), 
            form.getPassword(), 
            form.getUserName(), 
            Position.valueOf(form.getPosition()), 
            form.getWage(), 
            java.sql.Date.valueOf(form.getBirthDate()), 
            0, // 勤怠状況の初期値
            form.isEmploymentInsurance(), 
            form.getIsActive()
        );
        return "redirect:/admin/UserManagement";
    }
    
    /**
     * アカウント編集画面の表示
     * (userIdがString型であるため、PathVariableもStringに変更)
     */
    
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        // 1. DBから編集対象のユーザーを取得
        Optional<UserInfo> userOpt = accountService.findUserById(id);
        
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたユーザーが見つかりません。");
            return "redirect:/admin/UserManagement";
        }

        UserInfo userInfo = userOpt.get();

        // 2. Entity から DTO (Form) へデータを詰め替える
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId(userInfo.getUserId());
        form.setUserName(userInfo.getUserName());
        form.setPosition(userInfo.getPosition().name()); // EnumからString（"MANAGER"等）へ変換
        form.setWage(userInfo.getWage());
        
        // java.util.Date から java.time.LocalDate への型変換
        if (userInfo.getBirthDate() != null) {
            java.sql.Date sqlDate = new java.sql.Date(userInfo.getBirthDate().getTime());
            form.setBirthDate(sqlDate.toLocalDate());
        }
        
        form.setEmploymentInsurance(userInfo.isEmploymentInsurance());
        form.setIsActive(userInfo.getIsActive());
        // パスワードは画面初期表示時は空欄にする（変更時のみ入力させるため）
        form.setPassword("");

        model.addAttribute("userRegisterForm", form);
        
        return "account/admin/register";
    }

    /**
     * アカウント更新処理
     */
    @PostMapping("/admin/update")
    public String updateAccount(@Valid @ModelAttribute("userRegisterForm") UserRegisterForm form,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        // バリデーションエラーがある場合、再度編集画面（register.html）を返す
        if (result.hasErrors()) {
            return "account/admin/register";
        }

        // サービスの更新メソッドを呼び出す
        boolean isUpdated = accountService.updateAccount(form);
        
        if (!isUpdated) {
            redirectAttributes.addFlashAttribute("errorMessage", "更新対象のユーザーが存在しません。");
            return "redirect:/admin/UserManagement";
        }

        redirectAttributes.addFlashAttribute("successMessage", "アカウント情報を更新しました。");
        return "redirect:/admin/UserManagement";
    }
}