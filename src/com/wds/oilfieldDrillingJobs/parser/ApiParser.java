package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wds.oilfieldDrillingJobs.api.ApiResponse;

public abstract class ApiParser {
	
	protected ApiResponse apiResponse;
	
	public ApiParser() {
		apiResponse = new ApiResponse();
	}
	
	public ApiResponse getApiResponse() {
		return apiResponse;
	}
	
	public void setApiResponse(ApiResponse apiResponse) {
		this.apiResponse = apiResponse;
	}
	
	public void parse(Context context, InputStream is) {
		Object data = readData(context, is);
		apiResponse.setData(data);
	}
	
	public abstract Object readData(Context context, InputStream is);
	
	protected void checkForError(String json) {
		try {
			JSONObject jsonObj = new JSONObject(json);
			if (jsonObj.has("result")) {
				boolean result = jsonObj.optBoolean("result", true);
				if (!result) {
					setStatusError("Invalid response");
				}
			}
		} catch (JSONException e) {
			// possibly we have JSONArray as response, so everything is OK
		}		
	}
	
	protected void setStatusError(String message) {
		apiResponse.setStatus(ApiResponse.STATUS_ERROR);
		apiResponse.setError(message);
	}

}
