package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.wds.oilfieldDrillingJobs.model.Subscription;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class SubscriptionParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		Subscription subscription = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("api", json);
			JSONObject jsonObj = new JSONObject(json);
			subscription = new Subscription(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subscription;
	}

}
