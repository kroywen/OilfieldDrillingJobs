package com.wds.oilfieldDrillingJobs.screen;

import java.util.TimeZone;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.model.EmailSetting;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class RegisterScreen extends BaseScreen implements OnClickListener {
	
	protected EditText email;
	protected View registerBtn;
	protected View cancelBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);
		initializeViews();
	}
	
	protected void initializeViews() {
		email = (EditText) findViewById(R.id.email);
		
		registerBtn = (Button) findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(this);
		
		cancelBtn = findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancelBtn:
			Intent intent = new Intent(this, MainScreen.class);
			startActivity(intent);
			finish();
			break;
		case R.id.registerBtn:
			hideSoftKeyboard();
			register();
			break;
		}
	}
	
	protected void register() {
		String emailAddress = email.getText().toString().trim();
		String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		String timezone = TimeZone.getDefault() != null ? TimeZone.getDefault().getID() : null;
		if (!TextUtils.isEmpty(emailAddress)) {
			if (Utilities.isConnectionAvailable(this)) {
				Intent intent = new Intent(this, ApiService.class);
				intent.setData(Uri.parse(ApiData.COMMAND_EMAILS));
				intent.setAction(ApiData.METHOD_POST);
				intent.putExtra(ApiData.EMAIL, emailAddress);
				intent.putExtra(ApiData.NOTIFICATION_ON, true);
				intent.putExtra(ApiData.IS_PURCHASED, false);
				intent.putExtra(ApiData.DEVICE_ID, deviceId);
				intent.putExtra(ApiData.TIMEZONE, timezone);
				startService(intent);
				showProgressDialog(R.string.registering_email);
			} else {
				showConnectionErrorDialog();
			}
		} else {
			showInfoDialog(R.string.error, R.string.enter_email);
		}
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_EMAILS.equalsIgnoreCase(command) && ApiData.METHOD_POST.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
						EmailSetting emailSetting = (EmailSetting) apiResponse.getData();
						DrillingJobsApp.setEmailSetting(emailSetting);
						settings.setString(Settings.EMAIL, emailSetting.getEmail());
						settings.setString(Settings.UUID, emailSetting.getUuid());
						
						Intent intent = new Intent(this, MainScreen.class);
						startActivity(intent);
						finish();
					} else {
						showInfoDialog(getString(R.string.error), apiResponse.getError());
					}
				}
			}
		}
	}

}
