package com.wds.oilfieldDrillingJobs.model;

import org.json.JSONObject;

import com.wds.oilfieldDrillingJobs.api.ApiData;

public class Device {
	
	private String deviceId; // SECURE_ID
	private String email;
	private String timezone;
	private String lastSyncAt;
	private String deviceType; // android
	private String createdAt;
	private String updatedAt;
	
	public Device(JSONObject obj) {
		deviceId = obj.optString(ApiData.DEVICE_ID);
		email = obj.optString(ApiData.EMAIL);
		timezone = obj.optString(ApiData.TIMEZONE);
		lastSyncAt = obj.optString(ApiData.LAST_SYNC_AT);
		deviceType = obj.optString(ApiData.DEVICE_TYPE);
		createdAt = obj.optString(ApiData.CREATED_AT);
		updatedAt = obj.optString(ApiData.UPDATED_AT);
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLastSyncAt() {
		return lastSyncAt;
	}

	public void setLastSyncAt(String lastSyncAt) {
		this.lastSyncAt = lastSyncAt;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
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
