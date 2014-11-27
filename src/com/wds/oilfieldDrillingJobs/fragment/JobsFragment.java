package com.wds.oilfieldDrillingJobs.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

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
import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.screen.JobDetailsScreen;
import com.wds.oilfieldDrillingJobs.screen.MainScreen;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class JobsFragment extends BaseFragment implements OnItemClickListener {
	
	private PullToRefreshListView list;
	private View emptyFirstTime;
	private View empty;
	
	private List<Job> jobs;
	private JobsSectionedAdapter adapter;
	private String lastSyncAtDate;
	private String searchQuery;	
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.jobs_fragment, null);
		initializeViews(view);
		updateViews();
		
		if (Utilities.isConnectionAvailable(getActivity())) {
			list.postDelayed(new Runnable() {
				@Override
				public void run() {
					loadJobs();
				}
			}, 1000);
		} else {
			showConnectionErrorDialog();
		}
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.jobs_fragment_actions, menu);
	    
	    SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				hideSoftKeyboard();
				searchQuery = query;
				updateViews();
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				if (TextUtils.isEmpty(newText)) {
					searchQuery = newText;
					updateViews();
				}
				return false;
			}
		});
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
	}
	
	private void initializeViews(View view) {
		list = (PullToRefreshListView) view.findViewById(R.id.list);
		list.setOnItemClickListener(this);
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
		empty = view.findViewById(R.id.empty);
		emptyFirstTime = view.findViewById(R.id.emptyFirstTime);
	}
	
	private void updateViews() {
		boolean firstStart = TextUtils.isEmpty(settings.getString(Settings.LAST_SYNC_AT));
		jobs = DrillingJobsApp.getJobs();
		filterJobsWithSearchQuery();
		
		if (firstStart) {
			showLayout(emptyFirstTime);
		} else if (Utilities.isEmpty(jobs)) {
			showLayout(empty);
		} else {
			showLayout(list);
			list.onRefreshComplete();
			adapter = new JobsSectionedAdapter(getActivity(), jobs);
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
	
	private void filterJobsWithSearchQuery() {
		if (Utilities.isEmpty(jobs) || TextUtils.isEmpty(searchQuery)) {
			return;
		}
		List<Job> newList = new ArrayList<Job>();
		for (Job job : jobs) {
			if (job.hasTitle() && job.getTitle().contains(searchQuery) ||
				job.hasLocation() && job.getLocation().contains(searchQuery) ||
				job.hasDescription() && job.getDescription().contains(searchQuery)) {
					newList.add(job);
				}
		}
		jobs = newList;
	}
	
	private void showLayout(View layout) {
		list.setVisibility(layout == list ? View.VISIBLE : View.INVISIBLE);
		empty.setVisibility(layout == empty ? View.VISIBLE : View.INVISIBLE);
		emptyFirstTime.setVisibility(layout == emptyFirstTime ? View.VISIBLE : View.INVISIBLE);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_JOBS.equalsIgnoreCase(command) && ApiData.METHOD_GET.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {						
						List<Job> jobs = (List<Job>) apiResponse.getData();
						dbStorage.synchronizeJobs(jobs);
						DrillingJobsApp.setJobs(dbStorage.getJobs());
						
						Activity activity = getActivity();
						if (activity != null) {
							((MainScreen) activity).setMenuAdapter();
						}
						
						settings.setString(Settings.LAST_SYNC_AT, lastSyncAtDate);
						updateViews();
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
			((MainScreen) getActivity()).setMenuAdapter();
			
			Intent intent = new Intent(getActivity(), JobDetailsScreen.class);
			intent.putExtra(Job.UUID, j.getUuid());
			startActivity(intent);
		}
	}

}
