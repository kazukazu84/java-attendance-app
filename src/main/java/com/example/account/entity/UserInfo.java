package com.example.account.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity @Table(name = "user_info")
@Data
public class UserInfo {
	@Id
	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Enumerated(EnumType.STRING)
	@Column(name = "position", nullable = false)
	private Position position;

	//@Column(name = "wage", nullable = false)
	//private int wage;

    // ================= [リレーションシップの追加] =================
    @ManyToOne // 💡 複数のUserInfoに対して、1つのWageが紐付く（多対一）
    @JoinColumn(name = "wage_id", nullable = false) // 💡 データベース上の外部キー列「wage_id」を指定
    private Wage wage; // 💡 単なるID数値ではなく、Wageエンティティオブジェクトとして保持する
    // ==========================================================
	
	@Column(name = "birth_date", nullable = false)
	private Date birthDate;								

	@Column(name = "attendance_status", nullable = false)
	private int attendanceStatus;

	public String getAttendanceStatusStr() {
		return this.attendanceStatus == 1 ? "出勤" : "退勤";
	}
	
	@Column(name = "is_employment_insurance", nullable = false)
	private boolean isEmploymentInsurance;

	// 💡 雇用保険の表示ロジックをJavaに集約
	public String getEmploymentInsuranceStr() {
		return this.isEmploymentInsurance ? "対象" : "除外";
	}

	@Column(name = "is_active", nullable = false)
	private int isActive;

	public String getIsActiveStr() {
		return switch (this.isActive) {
		case 1 -> "有効";
		case 0 -> "無効";  // 先ほどサービス側で setIsActive(0) にしたので、0を無効にします
		default -> "不明";
		};
	}
}