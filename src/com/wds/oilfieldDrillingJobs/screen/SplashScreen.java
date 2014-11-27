package com.wds.oilfieldDrillingJobs.screen;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.model.EmailSetting;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class SplashScreen extends BaseScreen {
	
	public static final int STOPSPLASH = 0;
	public static final long SPLASHTIME = 2000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		if (Utilities.isConnectionAvailable(this)) {
			loadEmailSettings();
		} else {
			sendStopMessage();
		}
	}
	
	private void sendStopMessage() {
		Message msg = new Message();  
        msg.what = STOPSPLASH;  
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}
	
	@Override
	public void onBackPressed() {}
	
	@SuppressLint("HandlerLeak")
	private Handler splashHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {
        	if (msg.what == STOPSPLASH) {
        		startNextScreen();
        	}
        }
    }; 
    
    private void startNextScreen() {
    	String lastSyncAt = settings.getString(Settings.LAST_SYNC_AT);
		if (!DrillingJobsApp.isRegistered() && TextUtils.isEmpty(lastSyncAt)) {
			startRegisterScreen();
		} else {
			startMainScreen();
		}
    }
    
    private void startRegisterScreen() {
    	Intent intent = new Intent(this, RegisterScreen.class);
		startActivity(intent);
		finish();
    }
        
    private void startMainScreen() {
    	Intent intent = new Intent(this, MainScreen.class);
		startActivity(intent);
		finish();
    }
    
    private void loadEmailSettings() {
    	Intent intent = new Intent(this, ApiService.class);
    	intent.setData(Uri.parse(ApiData.COMMAND_EMAILS));
    	intent.setAction(ApiData.METHOD_GET);
    	intent.putExtra(ApiData.EMAIL, settings.getString(Settings.EMAIL));
    	startService(intent);
    }
    
    @Override
    public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_EMAILS.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
						EmailSetting emailSetting = (EmailSetting) apiResponse.getData();
						DrillingJobsApp.setEmailSetting(emailSetting);
						settings.setString(Settings.EMAIL, emailSetting.getEmail());
						settings.setString(Settings.UUID, emailSetting.getUuid());
					}
					startNextScreen();
				}
			}
		}
    }

}
