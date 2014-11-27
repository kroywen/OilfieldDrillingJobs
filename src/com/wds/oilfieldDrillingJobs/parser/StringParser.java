package com.wds.oilfieldDrillingJobs.parser;

import java.io.InputStream;

import android.content.Context;
import android.util.Log;

import com.wds.oilfieldDrillingJobs.util.Utilities;

public class StringParser extends ApiParser {

	@Override
	public Object readData(Context context, InputStream is) {
		String response = Utilities.streamToString(is);
		Log.d("api", response);
		return response;
	}

}