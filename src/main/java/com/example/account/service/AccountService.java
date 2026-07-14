package com.example.account.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.dto.UserRegisterForm;
import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;

@Service
public class AccountService {
	@Autowired private UserInfoRepository userRepo;
	@Autowired private PasswordEncoder passwordEncoder;

	 /**
     * IDによるユーザー検索
     */
    public Optional<UserInfo> findUserById(String id) {
        return userRepo.findById(id);
    }

    /**
     * 新規アカウント登録
     */
    @Transactional
    public void registerAccount(
        String id, 
        String rawPassword, 
        String userName, 
        Position position, 
        int wage, 
        Date birthDate, 
        int attendanceStatus, 
        boolean isEmploymentInsurance, 
        int isActive
    ) {
        UserInfo user = new UserInfo();
        
        user.setUserId(id);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setUserName(userName);
        user.setPosition(position);
        user.setWage(wage);
        user.setBirthDate(birthDate);
        user.setAttendanceStatus(attendanceStatus);
        user.setEmploymentInsurance(isEmploymentInsurance);
        user.setIsActive(isActive);
        userRepo.save(user);
    }

    /**
     * 既存アカウント更新
     * @return 更新成否 (対象ユーザーが存在しなかった場合はfalse)
     */
    @Transactional
    public boolean updateAccount(UserRegisterForm form) {
        Optional<UserInfo> userOpt = userRepo.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            return false;
        }

        UserInfo user = userOpt.get();
        
        // 1. 各項目の更新（パスワード以外）
        user.setUserName(form.getUserName());
        user.setPosition(Position.valueOf(form.getPosition()));
        user.setWage(form.getWage());
        
        if (form.getBirthDate() != null) {
            user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
        }
        
        user.setEmploymentInsurance(form.isEmploymentInsurance());
        user.setIsActive(form.getIsActive());

        // 2. パスワードの更新（画面で新しいパスワードが入力されている場合のみ暗号化して更新）
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        userRepo.save(user);
        return true;
    }

	@Transactional // 💡 複数データを一括更新するため、トランザクション管理下におくのが鉄則！
	public void deactivateUsers(List<String> userIds) {
		// 1. 対象のユーザーをデータベースから一括で取得する
		List<UserInfo> users = userRepo.findAllById(userIds);

		// 2. 取得したユーザーの在籍ステータス（active）をすべて「0 (無効/退職)」に書き換える
		for (UserInfo user : users) {
			user.setIsActive(0);
		}

		// 3. 変更内容をデータベースに一括保存（更新）する
		userRepo.saveAll(users);
	}

	public List<UserInfo> searchUsers(String keyword, String type) { // 💡 戻り値を UserInfo に変更
		if ("id".equals(type)) {
			return userRepo.findByUserIdContaining(keyword);
		} else if ("name".equals(type)) {
			return userRepo.findByUserNameContaining(keyword);
		}
		return userRepo.findAll();
	}

	
    
	/*
	@Transactional
	public void registerAccount(
			String id, 
			String rawPassword, 
			String userName, 
			Position position, 
			int wage, 
			Date birthDate, 
			int attendanceStatus, 
			boolean isEmploymentInsurance, 
			int isActive
			) {
		UserInfo user = new UserInfo();

		// 1. 既存の基本パーツ（パスワードはしっかり暗号化）
		user.setUserId(id);
		user.setPassword(passwordEncoder.encode(rawPassword));

		// 2. 💡 順番通りにすべて詰め込む！
		user.setUserName(userName);
		user.setPosition(position); // 引数側がすでにEnum（Position）なのでそのままでOK！
		user.setWage(wage);
		user.setBirthDate(birthDate);
		user.setAttendanceStatus(attendanceStatus);
		user.setEmploymentInsurance(isEmploymentInsurance);
		user.setIsActive(isActive);

		// 3. DBへ保存
		userRepo.save(user);
	}


	
	public UserInfo findUserById(String userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("対象のユーザーが見つかりません: " + userId));
    }

	/**
     * 🔍 編集画面用に、UserInfo を UserRegisterForm に詰め替えて取得する
     */
	/*
    public UserRegisterForm getUserRegisterFormById(String userId) {
        UserInfo user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("ユーザー不在: " + userId));
        
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId(user.getUserId());
        form.setPassword(user.getPassword());
        form.setUserName(user.getUserName());
        form.setPosition(user.getPosition().name()); // Enum から String へ変換
        form.setWage(user.getWage());
        form.setEmploymentInsurance(user.isEmploymentInsurance());
        form.setIsActive(user.getIsActive());
        
        // 💡 Date型 から LocalDate型 への美しき変換の術式
        if (user.getBirthDate() != null) {
            LocalDate localDate = user.getBirthDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            form.setBirthDate(localDate);
        }
        
        return form;
    }

    /**
     * 💾 画面からの入力を既存のエンティティに安全に詰め替えて更新する
     */
	/*
    @Transactional
    public void updateUser(UserRegisterForm form) {
        // 1. まずはDBから既存の完全なデータを引き抜く（防衛陣）
        UserInfo user = userRepo.findById(form.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("ユーザー不在: " + form.getUserId()));
        
        // 2. 変更可能な項目だけを安全に詰め替える
        user.setUserName(form.getUserName());
        user.setWage(form.getWage());
        user.setEmploymentInsurance(form.isEmploymentInsurance());
        user.setIsActive(form.getIsActive());
        
        // 💡 String から Position(Enum) への再変換
        if (form.getPosition() != null) {
            user.setPosition(Position.valueOf(form.getPosition()));
        }
        
        // 💡 LocalDate型 から Date型 への美しき変換の術式
        if (form.getBirthDate() != null) {
            Date date = Date.from(form.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            user.setBirthDate(date);
        }
        
        // 3. パスワード変更の制御（空で届いたら元のパスワードを維持する安全ガード）
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            user.setPassword(form.getPassword()); 
        }
        
        // トランザクション（@Transactional）の結界内なので、
        // このままメソッドが終了すれば自動的にDBにUPDATE文が放たれるぜ！
    }
*/
}