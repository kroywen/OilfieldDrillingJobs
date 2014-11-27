package com.wds.oilfieldDrillingJobs.model;

import java.io.Serializable;

import org.json.JSONObject;

import android.text.TextUtils;

import com.wds.oilfieldDrillingJobs.api.ApiData;

public class EmailSetting implements Serializable {

	private static final long serialVersionUID = 5715797578532689914L;
	
	private String uuid;
	private String email;
	private boolean notificationOn;
	private boolean isPurchased;
	private String createdAt;
	private String updatedAt;
	
	public EmailSetting(JSONObject obj) {
		uuid = obj.optString(ApiData.UUID);
		email = obj.optString(ApiData.EMAIL);
		notificationOn = obj.optBoolean(ApiData.NOTIFICATION_ON);
		isPurchased = obj.optBoolean(ApiData.IS_PURCHASED);
		createdAt = obj.optString(ApiData.CREATED_AT);
		updatedAt = obj.optString(ApiData.UPDATED_AT);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean hasEmail() {
		return !TextUtils.isEmpty(email);
	}

	public boolean isNotificationOn() {
		return notificationOn;
	}

	public void setNotificationOn(boolean notificationOn) {
		this.notificationOn = notificationOn;
	}

	public boolean isPurchased() {
		return isPurchased;
	}

	public void setPurchased(boolean isPurchased) {
		this.isPurchased = isPurchased;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

}
