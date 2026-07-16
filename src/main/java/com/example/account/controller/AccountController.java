package com.example.account.controller;

import java.util.List;
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
import com.example.account.entity.Wage;
import com.example.account.repository.WageRepository;
import com.example.account.service.AccountService;

@Controller
public class AccountController {
    
    @Autowired 
    private AccountService accountService;
    @Autowired
    private WageRepository wageRepo; // 💡 プルダウン用のデータ取得のために追加
    /**
     * アカウント登録画面の表示
     * (Thymeleafのth:objectエラー防止のため、空のFormオブジェクトをモデルに格納します)
     */
    @GetMapping("/admin/register")
    public String registerView(Model model) { 
        model.addAttribute("userRegisterForm", new UserRegisterForm());
        List<Wage> wages = wageRepo.findAllByOrderByWageValueAsc();
        model.addAttribute("wages", wages);
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
            form.getWageId(), // 💡 form.getWage() から getWageId() に変更
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
        
     // 💡 1. どこからでも現在のログインユーザーのIDを直接吸い出す！
        String loginUserId = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

        // 🛡️ 2. 【編集画面の直打ち防止】もし編集しようとしている「id」が自分自身なら、即・アクセス拒否！
        if (id.equals(loginUserId)) {
            // 既存の error-denied 画面に引き渡すメッセージをセット
            model.addAttribute("errorMessage", "現在ログイン中の自分自身を編集・無効化することはできませんぞい！");
            return "account/error-denied"; // 💡 即座にアクセス拒否画面を表示！
        }
        
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
        form.setPosition(userInfo.getPosition().name());
        
        // 💡 結合しているWageオブジェクトから主キー(ID)を取得してFormにセット
        if (userInfo.getWage() != null) {
            form.setWageId(userInfo.getWage().getWageId());
        }
        
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
        // 💡 編集画面を開くときも同様に、プルダウン用の賃金リストをモデルに格納する
        List<Wage> wages = wageRepo.findAllByOrderByWageValueAsc();
        model.addAttribute("wages", wages);
        return "account/admin/register";
    }

    /**
     * アカウント更新処理
     */
    @PostMapping("/admin/update")
    public String updateAccount(@Valid @ModelAttribute("userRegisterForm") UserRegisterForm form,
                                BindingResult result,
                                Model model, // 💡 バリデーションエラー時の再表示用にModelを追加
                                RedirectAttributes redirectAttributes) {

        // 💡 1. どこからでも現在のログインユーザーのIDを直接吸い出す！
        String loginUserId = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

        // 🛡️ 2. 【編集画面の直打ち防止】もし編集しようとしている「id」が自分自身なら、即・アクセス拒否！
        if (form.getUserId().equals(loginUserId)) {
            // 既存の error-denied 画面に引き渡すメッセージをセット
            model.addAttribute("errorMessage", "現在ログイン中の自分自身を編集・無効化することはできませんぞい！");
            return "account/error-denied"; // 💡 即座にアクセス拒否画面を表示！
        }
    	
    	// バリデーションエラーがある場合、再度編集画面（register.html）を返す
        if (result.hasErrors()) {
        	 // 💡 エラーで自画面に戻る際も、プルダウンの選択肢が消えないようにリストを再格納する
            List<Wage> wages = wageRepo.findAllByOrderByWageValueAsc();
            model.addAttribute("wages", wages);
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