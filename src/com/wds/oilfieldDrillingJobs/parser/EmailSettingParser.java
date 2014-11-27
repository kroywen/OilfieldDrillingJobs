package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.model.EmailSetting;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class EmailSettingParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		EmailSetting emailSetting = null;
		String json = Utilities.streamToString(is);
		Log.d("api", json);
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONObject jsonObj = new JSONObject(json);
				emailSetting = new EmailSetting(jsonObj);
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		
		return emailSetting;
	}

}
