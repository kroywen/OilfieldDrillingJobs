package com.wds.oilfieldDrillingJobs.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	
	public static final String FILENAME = "OilfieldDrillingJobs";
	
	public static final String LAST_SYNC_AT = "last_sync_at";
	public static final String EMAIL = "email";
	public static final String UUID = "uuid";
	public static final String JOB_HISTORY_DAYS = "job_history_days";
	
	private SharedPreferences prefs;
	
	public Settings(Context context) {
		prefs = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
	}
	
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}
	
	public String getString(String key) {
		return getString(key, null);
	}
	
	public void setString(String key, String value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putString(key, value);
		e.commit();
	}
	
	public int getInt(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}
	
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	public void setInt(String key, int value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putInt(key, value);
		e.commit();
	}
	
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public void setBoolean(String key, boolean value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putBoolean(key, value);
		e.commit();
	}
	
	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}
	
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}
	
	public void setFloat(String key, float value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putFloat(key, value);
		e.commit();
	}
	
	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}
	
	public long getLong(String key) {
		return getLong(key, 0L);
	}
	
	public void setLong(String key, long value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putLong(key, value);
		e.commit();
	}

}
