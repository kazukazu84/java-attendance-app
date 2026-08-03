package com.example.adminshift.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shift_application_setting")
public class ShiftApplicationSetting {
    // getter/setter


	    public Integer getSettingId() {
		return settingId;
	}

	public void setSettingId(Integer settingId) {
		this.settingId = settingId;
	}

	public Integer getTargetWeeks() {
		return targetWeeks;
	}

	public void setTargetWeeks(Integer targetWeeks) {
		this.targetWeeks = targetWeeks;
	}

	public Integer getApplicationStartDays() {
		return applicationStartDays;
	}

	public void setApplicationStartDays(Integer applicationStartDays) {
		this.applicationStartDays = applicationStartDays;
	}

	public Integer getApplicationEndDays() {
		return applicationEndDays;
	}

	public void setApplicationEndDays(Integer applicationEndDays) {
		this.applicationEndDays = applicationEndDays;
	}

		@Id
	    private Integer settingId;

	    private Integer targetWeeks;

	    private Integer applicationStartDays;

	    private Integer applicationEndDays;

	}


