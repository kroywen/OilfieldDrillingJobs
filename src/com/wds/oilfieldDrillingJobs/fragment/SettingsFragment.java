package com.wds.oilfieldDrillingJobs.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.screen.AboutScreen;
import com.wds.oilfieldDrillingJobs.screen.OtherAppsScreen;
import com.wds.oilfieldDrillingJobs.screen.RegisterScreen2;
import com.wds.oilfieldDrillingJobs.storage.Settings;

public class SettingsFragment extends BaseFragment implements OnClickListener {
	
	private View registeredEmailBtn;
	private View jobListHistoryBtn;
	private View supportAndFeedbackBtn;
	private View rateAppBtn;
	private View shareAppBtn;
	private View aboutBtn;
	private View otherAppsBtn;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, null);
		initializeViews(view);
		return view;
	}
	
	private void initializeViews(View view) {
		registeredEmailBtn = view.findViewById(R.id.registeredEmailBtn);
		registeredEmailBtn.setOnClickListener(this);
		
		jobListHistoryBtn = view.findViewById(R.id.jobListHistoryBtn);
		jobListHistoryBtn.setOnClickListener(this);
		
		supportAndFeedbackBtn = view.findViewById(R.id.supportAndFeedbackBtn);
		supportAndFeedbackBtn.setOnClickListener(this);
		
		rateAppBtn = view.findViewById(R.id.rateAppBtn);
		rateAppBtn.setOnClickListener(this);
		
		shareAppBtn = view.findViewById(R.id.shareAppBtn);
		shareAppBtn.setOnClickListener(this);
		
		aboutBtn = view.findViewById(R.id.aboutBtn);
		aboutBtn.setOnClickListener(this);
		
		otherAppsBtn = view.findViewById(R.id.otherAppsBtn);
		otherAppsBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registeredEmailBtn:
			Intent intent = new Intent(getActivity(), RegisterScreen2.class);
			startActivityForResult(intent, DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE);
			break;
		case R.id.jobListHistoryBtn:
			showJobListHistoryDialog();
			break;
		case R.id.supportAndFeedbackBtn:
			sendFeedback();
			break;
		case R.id.rateAppBtn:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
			startActivity(Intent.createChooser(intent, getString(R.string.open_via)));
			break;
		case R.id.shareAppBtn:
			intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text));
			intent.setType("text/plain");
			startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
			break;
		case R.id.aboutBtn:
			intent = new Intent(getActivity(), AboutScreen.class);
			startActivity(intent);
			break;
		case R.id.otherAppsBtn:
			intent = new Intent(getActivity(), OtherAppsScreen.class);
			startActivity(intent);
			break;
		}
	}
	
	private void sendFeedback() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"oilfielddrilling@wellsite-ds.com"});
		startActivity(Intent.createChooser(intent, getString(R.string.open_via)));
	}
	
	private void showJobListHistoryDialog() {
		int historyChecked = settings.getInt(Settings.JOB_HISTORY_DAYS, 0); 
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setSingleChoiceItems(R.array.history_items, historyChecked, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					settings.setInt(Settings.JOB_HISTORY_DAYS, which);
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}

}
