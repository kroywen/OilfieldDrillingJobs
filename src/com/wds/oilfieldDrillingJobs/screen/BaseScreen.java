package com.wds.oilfieldDrillingJobs.screen;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiResponseReceiver;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.api.OnApiResponseListener;
import com.wds.oilfieldDrillingJobs.dialog.InfoDialog;
import com.wds.oilfieldDrillingJobs.dialog.ProgressDialog;
import com.wds.oilfieldDrillingJobs.storage.DatabaseStorage;
import com.wds.oilfieldDrillingJobs.storage.Settings;

public class BaseScreen extends FragmentActivity implements OnApiResponseListener {
	
	public static final String APP_TAG = "OILFIELD_DRILLING_JOBS"; 
	
	protected ApiResponseReceiver responseReceiver;
	protected ProgressDialog progressDialog;
	
	protected Settings settings;
	protected DatabaseStorage dbStorage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = new Settings(this);
		dbStorage = DatabaseStorage.getInstance(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		responseReceiver = new ApiResponseReceiver(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(
			responseReceiver, new IntentFilter(ApiService.ACTION_API_RESULT));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
	}
	
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {}
	
	public void showProgressDialog(int messageResId) {
		showProgressDialog(getString(messageResId));
	}
	
	public void showProgressDialog(String message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog();
		}
		progressDialog.setText(message);
		if (!progressDialog.isVisible() && !progressDialog.isAdded()) {
			try {
				progressDialog.show(getFragmentManager(), "ProgressDialog");
			} catch (Exception e) { // IllegalStateException: Can not perform this action after onSaveInstanceState
				e.printStackTrace();
			}
		}
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showConnectionErrorDialog() {
		showInfoDialog(R.string.information, R.string.no_connection);
	}
	
	public void showInfoDialog(int titleResId, int messageResId) {
		showInfoDialog(getString(titleResId), getString(messageResId));
	}
	
	public void showInfoDialog(String title, String message) {
		InfoDialog dialog = new InfoDialog();
		dialog.setTitle(title);
		dialog.setText(message);
		try {
			dialog.show(getFragmentManager(), "InfoDialog");
		} catch (Exception e) { // IllegalStateException: Can not perform this action after onSaveInstanceState
			e.printStackTrace();
		}
	}
	
	public void hideSoftKeyboard() {
		View view = getCurrentFocus();
		if (view == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void log(String tag, String message) {
		Log.d(tag, message);
	}
	
	public static void log(String message) {
		log(APP_TAG, message);
	}

}
