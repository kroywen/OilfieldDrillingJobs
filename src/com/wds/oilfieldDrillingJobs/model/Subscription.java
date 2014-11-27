package com.wds.oilfieldDrillingJobs.model;

import org.json.JSONObject;

import android.text.TextUtils;

import com.wds.oilfieldDrillingJobs.api.ApiData;

public class Subscription {
	
	private String uuid;
	private String email;
	private String subType; // title or location
	private String keyword;
	private String createdAt;
	private String updatedAt;
	
	public Subscription(JSONObject obj) {
		uuid = obj.optString(ApiData.UUID);
		email = obj.optString(ApiData.EMAIL);
		subType = obj.optString(ApiData.SUB_TYPE);
		keyword = obj.optString(ApiData.KEYWORD);
		createdAt = obj.optString(ApiData.CREATED_AT);
		updatedAt = obj.optString(ApiData.UPDATED_AT);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public boolean hasUuid() {
		return !TextUtils.isEmpty(uuid);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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
