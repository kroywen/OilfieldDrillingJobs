package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.wds.oilfieldDrillingJobs.model.Subscription;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class SubscriptionListParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		List<Subscription> subscriptions = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("api", json);
			JSONObject jsonObj = new JSONObject(json);
			JSONArray array = jsonObj.optJSONArray("results");
			if (array.length() > 0) {
				subscriptions = new ArrayList<Subscription>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Subscription subscription = new Subscription(obj);
					subscriptions.add(subscription);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subscriptions;
	}

}
