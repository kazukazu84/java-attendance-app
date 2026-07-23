package com.example.account.entity;

public enum Position {
	ADMIN("管理者"),
	USER("一般ユーザー");

	private final String description;

	Position(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
