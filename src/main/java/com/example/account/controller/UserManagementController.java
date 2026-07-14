package com.example.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
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
        return "redirect:/admin/UserManagement"; 
    }
	
    @PostMapping("/admin/users/batch-deactivate")
    public String batchDeactivate(@RequestParam(value = "userIds", required = false) List<String> userIds) {
        
        // 💡 何もチェックされずに届いた場合の安全ガード
        if (userIds != null && !userIds.isEmpty()) {
            // 次のステップで作る「サービス」の無効化メソッドを呼び出す
            accountService.deactivateUsers(userIds);
        }
        
        // 🔄 処理が終わったら、検索が解除された初期の一覧画面（URL）へリダイレクト！
        return "redirect:/admin/UserManagement"; 
    }
    
    /**
     * 👥 ユーザー編集画面を表示する (既存の UserRegisterForm を使用)
     */
    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable("id") String userId, Model model) {
        // 💡 サービスから、UserInfo を UserRegisterForm に詰め替えたデータを取得する
        UserRegisterForm form = accountService.getUserRegisterFormById(userId);
        
        // 💡 既存の新規登録画面が使っている変数名（"userInfo"）に合わせて積む！
        model.addAttribute("userInfo", form);
        model.addAttribute("isEdit", true);
        
        return "account/admin/register";
    }

    /**
     * 💾 ユーザー情報の更新処理（UserRegisterForm 直受け）
     */
    @PostMapping("/admin/users/update")
    public String updateUser(@ModelAttribute("userInfo") UserRegisterForm form) {
        // 💡 既存のDTOをそのままサービスへ流し込む！
        accountService.updateUser(form);
        
        return "redirect:/admin/UserManagement";
    }
    
    
    //詳細画面のコントローラに意向する予定。
    @GetMapping("/admin/register")
    public String showRegisterForm(Model model) {
        // 💡 新規登録の時も、空のUserInfoオブジェクトを入れておくことで画面のクラッシュを防ぐ！
        UserInfo userInfo = new UserInfo();
        userInfo.setWage(1200); // 初期値の時給1200円をここでセットするぜ！
        userInfo.setIsActive(1); // 初期ステータス「在籍」
        
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("isEdit", false); // 編集モードではないフラグ
        return "account/admin/register";
    }
    
	@PostMapping("/admin/register")
	public String register(@ModelAttribute UserRegisterForm form) {
		accountService.registerAccount(
		        form.getUserId(), 
		        form.getPassword(), 
		        form.getUserName(), 
		        // 💡 DTOのStringから、Serviceが求めている「Position（Enum）」に変換してぶち込む！
		        Position.valueOf(form.getPosition()), 
		        form.getWage(), 
		        // 💡 DTOのLocalDateから、Serviceが求めている「java.util.Date」に変換してぶち込む！
		        java.sql.Date.valueOf(form.getBirthDate()), 
		        0, // 💡 勤怠状況（attendanceStatus）は初期値「0（退勤）」固定で安全にセット！
		        form.isEmploymentInsurance(), // 💡 Lombokが作ったゲッターで綺麗に渡せる！
		        form.getIsActive()
		    );
	    return "redirect:/admin/UserManagement";
	}
}
