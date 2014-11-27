package com.wds.oilfieldDrillingJobs.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.wds.oilfieldDrillingJobs.parser.ApiParser;
import com.wds.oilfieldDrillingJobs.parser.ParserFactory;

public class ApiService extends IntentService {
	
	public static final String ACTION_API_RESULT = "action_api_result";
	public static final String EXTRA_API_STATUS = "extra_api_status";
	public static final String EXTRA_API_RESPONSE = "extra_api_response";
	
	public static final int API_STATUS_NONE = -1;
	public static final int API_STATUS_SUCCESS = 0;
	public static final int API_STATUS_ERROR = 1;
	
	public ApiService() {
		this(ApiService.class.getSimpleName());
	}

	public ApiService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String command = intent.getData().toString();
		String method = intent.getAction();
		
		AndroidHttpClient client = AndroidHttpClient.newInstance(
			System.getProperty("http.agent"), this);
		HttpRequestBase request = getHttpRequest(method);
		
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		
		String url = createURL(command, extras);
		Log.d("api", method + ": " + url);
		request.setURI(URI.create(url));
		
		Object body = extras.get(ApiData.PARAM_BODY);
		if (body != null && request instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase r = (HttpEntityEnclosingRequestBase) request;
			try {
				String bodyText = (String) body;
				Log.d("api", "Body: " + bodyText);
				r.setEntity(new StringEntity(bodyText, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				ApiParser parser = ParserFactory.getParser(command, method);
				if (parser != null) {
					parser.parse(this, is);
					ApiResponse apiResponse = parser.getApiResponse();
					apiResponse.setStatus(response.getStatusLine().getStatusCode());
					apiResponse.setMethod(method);
					apiResponse.setRequestName(command);
					sendResult(API_STATUS_SUCCESS, apiResponse);
				}
				
				is.close();
			} else {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setStatus(response.getStatusLine().getStatusCode());
				apiResponse.setMethod(method);
				apiResponse.setRequestName(command);
				sendResult(API_STATUS_SUCCESS, apiResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			sendResult(API_STATUS_ERROR, null);
		} finally {
			client.close();
		}
	}
	
	private HttpRequestBase getHttpRequest(String method) {
		if (method.equalsIgnoreCase(ApiData.METHOD_GET)) {
			return new HttpGet();
		} else if (method.equalsIgnoreCase(ApiData.METHOD_POST)) {
			return new HttpPost();
		} else if (method.equalsIgnoreCase(ApiData.METHOD_PUT)) {
			return new HttpPut();
		} else if (method.equalsIgnoreCase(ApiData.METHOD_DELETE)) {
			return new HttpDelete();
		} else {
			return null;
		}
	}
	
	private void sendResult(int apiStatus, ApiResponse apiResponse) {
		Intent resultIntent = new Intent(ACTION_API_RESULT);
		resultIntent.putExtra(EXTRA_API_STATUS, apiStatus);
		resultIntent.putExtra(EXTRA_API_RESPONSE, apiResponse);
		LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
	}
	
	private String createURL(String command, Bundle params) {
		String url = ApiData.BASE_URL + command;
		if (params.containsKey(ApiData.PARAM_ID)) {
			String id = params.getString(ApiData.PARAM_ID);
			if (params.containsKey(ApiData.PARAM_ID1)) {
				String id1 = params.getString(ApiData.PARAM_ID1);
				url = String.format(url, id, id1);
			} else {
				url = String.format(url, id);
			}			
		}
		
		Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
		if (params != null) {
			Set<String> keys = params.keySet();
			if (keys != null && !keys.isEmpty()) {
				Iterator<String> i = keys.iterator();
				while (i.hasNext()) {
					String key = i.next();
					if (key.equalsIgnoreCase(ApiData.PARAM_BODY) ||
						key.equalsIgnoreCase(ApiData.PARAM_ID) ||
						key.equalsIgnoreCase(ApiData.PARAM_ID1))
					{
						continue;
					}
					String value = String.valueOf(params.get(key));
					uriBuilder.appendQueryParameter(key, value);
				}
			}
		}
		String result = uriBuilder.build().toString();
		return result;
	}

}
