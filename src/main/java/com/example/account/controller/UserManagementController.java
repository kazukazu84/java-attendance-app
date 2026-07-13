package com.example.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        return "kumeda/admin/UserManagement"; 
    }
	@GetMapping("/admin/register")
	public String registerView() { return "kumeda/admin/register"; }

	@PostMapping("/register")
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
