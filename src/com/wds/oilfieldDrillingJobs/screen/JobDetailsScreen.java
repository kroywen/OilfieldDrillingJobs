package com.wds.oilfieldDrillingJobs.screen;

import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.fragment.JobDetailsFragment;
import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class JobDetailsScreen extends BaseScreen implements OnPageChangeListener {
	
	private ViewPager pager;
	private PagerAdapter adapter;
	
	private List<Job> jobs;
	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.job_details_screen);
		
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		getIntentData();
		jobs = DrillingJobsApp.getJobs();
		initializeViews();
		updateViews();
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			String uuid = intent.getStringExtra(Job.UUID);
			position = DrillingJobsApp.getJobPositionByUuid(uuid);
		}
	}
	
	private void initializeViews() {
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnPageChangeListener(this);
	}
	
	private void updateViews() {
		adapter = new JobDetailsFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.setCurrentItem(position);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.job_details_screen_actions, menu);
	    menu.findItem(R.id.action_prev).setIcon(position == 0 ? R.drawable.arrow_up_disabled : R.drawable.arrow_up_enabled);
	    menu.findItem(R.id.action_next).setIcon(position == jobs.size()-1 ? R.drawable.arrow_down_disabled : R.drawable.arrow_down_enabled);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
	    case R.id.action_prev:
	    	previousJob();
	    	return true;
	    case R.id.action_next:
	    	nextJob();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void previousJob() {
		if (position > 0) {
			position--;
			pager.setCurrentItem(position, true);
		}
	}
	
	private void nextJob() {
		if (position < jobs.size()-1) {
			position++;
			pager.setCurrentItem(position, true);
		}
	}
	
	private class JobDetailsFragmentPagerAdapter extends FragmentPagerAdapter {
		
		public JobDetailsFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
	    public Fragment getItem(int position) {
			String uuid = jobs.get(position).getUuid();
			return JobDetailsFragment.newInstance(uuid);
	    }

	    @Override
	    public int getCount() {
	    	return jobs.size();
	    }
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		this.position = position;
		invalidateOptionsMenu();
	}
	
	public void sendEmail(String uuids) {
		if (Utilities.isConnectionAvailable(this)) {
			Intent intent = new Intent(this, ApiService.class);
			intent.setData(Uri.parse(ApiData.COMMAND_SEND_EMAIL));
			intent.setAction(ApiData.METHOD_POST);
			
			try {
				JSONObject obj = new JSONObject();
				obj.put(ApiData.EMAIL, settings.getString(Settings.EMAIL));
				obj.put(ApiData.TRIGGER, "email_me");
				obj.put(ApiData.UUIDS, uuids);
				
				intent.putExtra(ApiData.PARAM_BODY, obj.toString());
				startService(intent);
				showProgressDialog(R.string.sending_email);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			showConnectionErrorDialog(); 
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
				if (ApiData.COMMAND_SEND_EMAIL.equalsIgnoreCase(command) && ApiData.METHOD_POST.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
						Boolean result = (Boolean) apiResponse.getData();
						if (result.booleanValue()) {
							showInfoDialog(R.string.information, R.string.emails_sent);
						} else {
							showInfoDialog(R.string.error, R.string.emails_send_error);
						}
					}
				}
			}
		}
	}

}
