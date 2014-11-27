package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class JobListParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		Settings settings = new Settings(context);
		List<Job> jobs = null;
		try {
			String json = Utilities.streamToString(is);
			Log.d("api", json);
			JSONObject jsonObj = new JSONObject(json);
			JSONArray array = jsonObj.optJSONArray("results");
			if (array.length() > 0) {
				jobs = new ArrayList<Job>();
				for (int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Job job = new Job(obj);
					long jobUpdatedTime = job.getUpdatedAtMillis();
					long nowTime = System.currentTimeMillis();
					
					int historyTime = settings.getInt(Settings.JOB_HISTORY_DAYS, 20) * 24 * 60 * 60 * 1000;
					if (nowTime - jobUpdatedTime < historyTime) {
						jobs.add(job);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs;
	}

}
