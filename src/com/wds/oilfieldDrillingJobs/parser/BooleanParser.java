package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.wds.oilfieldDrillingJobs.util.Utilities;

public class BooleanParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		boolean result = false;
		try {
			String json = Utilities.streamToString(is);
			Log.d("api", json);
			JSONObject jsonObj = new JSONObject(json);
			result = jsonObj.optBoolean("result", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
