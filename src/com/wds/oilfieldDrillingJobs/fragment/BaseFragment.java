package com.wds.oilfieldDrillingJobs.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiResponseReceiver;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.api.OnApiResponseListener;
import com.wds.oilfieldDrillingJobs.screen.BaseScreen;
import com.wds.oilfieldDrillingJobs.storage.DatabaseStorage;
import com.wds.oilfieldDrillingJobs.storage.Settings;

public class BaseFragment extends Fragment implements OnApiResponseListener {
	
	protected ApiResponseReceiver responseReceiver;
	protected Settings settings;
	protected DatabaseStorage dbStorage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		settings = new Settings(getActivity());
		dbStorage = DatabaseStorage.getInstance(getActivity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter(ApiService.ACTION_API_RESULT);
		responseReceiver = new ApiResponseReceiver(this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
			responseReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
	}

	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {}
	
	protected void showConnectionErrorDialog() {
		((BaseScreen) getActivity()).showConnectionErrorDialog();
	}
	
	protected void showProgressDialog(int messageResId) {
		((BaseScreen) getActivity()).showProgressDialog(messageResId);
	}
	
	protected void showProgressDialog(String message) {
		((BaseScreen) getActivity()).showProgressDialog(message);
	}
	
	protected void hideProgressDialog() {
		Activity activity = getActivity();
		if (activity != null) {
			try {
				((BaseScreen) activity).hideProgressDialog(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void showToast(String message) {
		((BaseScreen) getActivity()).showToast(message);
	}
	
	protected void hideSoftKeyboard() {
		((BaseScreen) getActivity()).hideSoftKeyboard();
	}

}
