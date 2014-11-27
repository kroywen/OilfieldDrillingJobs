package com.wds.oilfieldDrillingJobs.fragment;

import java.text.ParseException;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.adapter.JobsSectionedAdapter;
import com.wds.oilfieldDrillingJobs.adapter.JobsSectionedAdapter.ListItem;
import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.dialog.ConfirmationDialog;
import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.model.Subscription;
import com.wds.oilfieldDrillingJobs.screen.BaseScreen;
import com.wds.oilfieldDrillingJobs.screen.JobDetailsScreen;
import com.wds.oilfieldDrillingJobs.screen.MainScreen;
import com.wds.oilfieldDrillingJobs.screen.NotificationSetupScreen;
import com.wds.oilfieldDrillingJobs.screen.RegisterDialogScreen;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.storage.UuidsStorage;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class NotificationFragment extends BaseFragment implements OnItemClickListener {
	
	private PullToRefreshListView list;
	private View empty;
	
	private List<Job> notifications;
	private JobsSectionedAdapter adapter;
	private String lastSyncAtDate;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.notification_fragment, null);
		initializeViews(view);
		updateViews();
		
		if (DrillingJobsApp.isRegistered() && !DrillingJobsApp.hasSubscriptions()) {
			if (Utilities.isConnectionAvailable(getActivity())) {
				list.postDelayed(new Runnable() {
					@Override
					public void run() {
						loadSubscriptions();
						// load notifications from server instead of filtering it locally from jobs
					}
				}, 1000);
			} else {
				showConnectionErrorDialog();
			}
		}
		
		return view;
	}
	
	private void initializeViews(View view) {
		list = (PullToRefreshListView) view.findViewById(R.id.list);
		list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (Utilities.isConnectionAvailable(getActivity())) {
					loadJobs();
				} else {
					list.onRefreshComplete();
					showConnectionErrorDialog();
				}
			}
		});
		list.setOnItemClickListener(this);
		
		empty = view.findViewById(R.id.empty);
	}
	
	private void updateViews() {
		notifications = DrillingJobsApp.getNotifications(getActivity());
		getActivity().invalidateOptionsMenu();
		if (Utilities.isEmpty(notifications)) {
			showLayout(empty);
		} else {
			showLayout(list);
			list.onRefreshComplete();
			adapter = new JobsSectionedAdapter(getActivity(), notifications);
			list.setAdapter(adapter);
			
			try {
				String lastSyncAt = settings.getString(Settings.LAST_SYNC_AT, null);
				long lastSyncMillis = Utilities.parseDate(lastSyncAt, Utilities.yyyy_MM_ddTHH_mm_ss_S_Z);
				ILoadingLayout loadingLayout = list.getLoadingLayoutProxy();
				loadingLayout.setLastUpdatedLabel(getString(R.string.last_update_pattern, Utilities.timeAgo(getActivity(), lastSyncMillis)));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) { // Fragment JobsFragment not attached to Activity
				e.printStackTrace(); 
			}
		}
	}
	
	private void showLayout(View layout) {
		list.setVisibility(layout == list ? View.VISIBLE : View.INVISIBLE);
		empty.setVisibility(layout == empty ? View.VISIBLE : View.INVISIBLE);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.notifications_fragment_actions, menu);
		MenuItem clearItem = menu.findItem(R.id.action_clear);
		if (clearItem != null) {
			clearItem.setVisible(!Utilities.isEmpty(notifications));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_clear:
			if (!Utilities.isEmpty(notifications)) {
				showConfirClearNotificationsDialog();
			}
			return true;
		case R.id.action_setup:
			if (DrillingJobsApp.isRegistered()) {
				startNotificationSetupScreen();
			} else {
				Intent intent = new Intent(getActivity(), RegisterDialogScreen.class);
				startActivityForResult(intent, DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showConfirClearNotificationsDialog() {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.clear_notification_list));
		dialog.setText(getString(R.string.all_jobs_will_be_cleared));
		dialog.setOkListener(getString(R.string.ok), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				List<String> uuids = Utilities.getUuids(notifications);
				UuidsStorage.addUuids(getActivity(), settings.getString(Settings.UUID), uuids);
				((MainScreen) getActivity()).setMenuAdapter();
				updateViews();
			}
		});
		dialog.show(getFragmentManager(), "ClearNotificationsDialog");
	}
	
	private void startNotificationSetupScreen() {
		Intent intent = new Intent(getActivity(), NotificationSetupScreen.class);
		startActivityForResult(intent, DrillingJobsApp.SETUP_NOTIFICATIONS_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				startNotificationSetupScreen();
			} else {
				if (data != null && data.hasExtra("error")) {
					String error = data.getStringExtra("error");
					((BaseScreen) getActivity()).showInfoDialog(getString(R.string.error), error);
				}
			}
		} else if (requestCode == DrillingJobsApp.SETUP_NOTIFICATIONS_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				((MainScreen) getActivity()).setMenuAdapter();
				updateViews();
			}
		}
	}
	
	private void loadJobs() {
		String lastSyncAt = settings.getString(Settings.LAST_SYNC_AT);
		String start = TextUtils.isEmpty(lastSyncAt) ? "1970-01-01T00:00:00.000+0200" : lastSyncAt;
		String end = Utilities.parseTime(System.currentTimeMillis(), Utilities.yyyy_MM_ddTHH_mm_ss_S_Z);
		String deviceId = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
		
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.COMMAND_JOBS));
		intent.setAction(ApiData.METHOD_GET);
		intent.putExtra(ApiData.JOB_TYPE, "Drilling");
		intent.putExtra(ApiData.START, start);
		intent.putExtra(ApiData.END, end);
		if (!TextUtils.isEmpty(lastSyncAt)) {
			intent.putExtra(ApiData.UPDATED_AT, lastSyncAt);
		}
		intent.putExtra(ApiData.DEVICE_ID, deviceId);
		getActivity().startService(intent);
		
		list.setRefreshing();
		lastSyncAtDate = Utilities.parseTime(System.currentTimeMillis(), Utilities.yyyy_MM_ddTHH_mm_ss_S_Z);
	}
	
	private void loadSubscriptions() {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.COMMAND_SUBSCRIPTIONS));
		intent.setAction(ApiData.METHOD_GET);
		intent.putExtra(ApiData.EMAIL, settings.getString(Settings.EMAIL));
		getActivity().startService(intent);
		showProgressDialog(R.string.loading_subscription_list);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_SUBSCRIPTIONS.equalsIgnoreCase(command)) {
					if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
							List<Subscription> subscriptions = (List<Subscription>) apiResponse.getData();
							DrillingJobsApp.setSubscriptions(subscriptions);
							updateViews();
						}
					}
				} else if (ApiData.COMMAND_JOBS.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
						List<Job> jobs = (List<Job>) apiResponse.getData();
						dbStorage.synchronizeJobs(jobs);
						DrillingJobsApp.setJobs(dbStorage.getJobs());
						((MainScreen) getActivity()).setMenuAdapter();
						
						settings.setString(Settings.LAST_SYNC_AT, lastSyncAtDate);
						updateViews();
					}
				} else if (ApiData.COMMAND_SEND_EMAIL.equalsIgnoreCase(command) && ApiData.METHOD_POST.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) { // FIXME statusCode = 500 Internal Server Error
						Boolean result = (Boolean) apiResponse.getData();
						if (result.booleanValue()) {
							((BaseScreen) getActivity()).showInfoDialog(R.string.information, R.string.emails_sent);
						} else {
							((BaseScreen) getActivity()).showInfoDialog(R.string.error, R.string.emails_send_error);
						}
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ListItem item = adapter.getItem(position-1);
		if (item.getType() == ListItem.TYPE_JOB) {
			Job job = item.getJob();
			Job j = DrillingJobsApp.getJobByUuid(job.getUuid());
			j.setRead(true);
			job.setRead(true);
			dbStorage.updateJob(job);
			adapter.notifyDataSetChanged();
			
			Activity activity = getActivity();
			if (activity != null) {
				((MainScreen) activity).setMenuAdapter();
			}
			
			Intent intent = new Intent(getActivity(), JobDetailsScreen.class);
			intent.putExtra(Job.UUID, job.getUuid());
			startActivity(intent);
		}
	}

}
