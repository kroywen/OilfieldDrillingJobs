package com.wds.oilfieldDrillingJobs.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.adapter.JobsAdapter;
import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.screen.BaseScreen;
import com.wds.oilfieldDrillingJobs.screen.JobDetailsScreen;
import com.wds.oilfieldDrillingJobs.screen.MainScreen;
import com.wds.oilfieldDrillingJobs.screen.RegisterDialogScreen;
import com.wds.oilfieldDrillingJobs.storage.FavouritesStorage;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class FavouritesFragment extends BaseFragment implements OnItemClickListener {
	
	private ListView list;
	private View empty;
	
	private List<Job> favourites;
	private JobsAdapter adapter;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.favourites_fragment, null);
		initializeViews(view);
		updateViews();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().invalidateOptionsMenu();
	}
	
	private void initializeViews(View view) {
		list = (ListView) view.findViewById(R.id.list);
		list.setOnItemClickListener(this);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		list.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getActivity().getMenuInflater().inflate(R.menu.favourites_context_menu, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
	            case R.id.action_delete:
	                deleteSelectedItems();
	                mode.finish();
	                return true;
	            default:
	                return false;
				}
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				int checkedCount = list.getCheckedItemCount();
				mode.setTitle(getString(R.string.selected_pattern, checkedCount));
				
			}
		});		
		
		empty = view.findViewById(R.id.empty);
	}
	
	private void deleteSelectedItems() {
		int checkedCount = list.getCheckedItemCount();
		if (checkedCount > 0 && adapter != null && adapter.getCount() > 0) {
			List<Job> selected = new ArrayList<Job>();
			for (int i=0; i<adapter.getCount(); i++) {
				if (list.isItemChecked(i)) {
					selected.add(adapter.getItem(i));
				}
			}
			String userUuid = settings.getString(Settings.UUID);
			for (int i=0; i<selected.size(); i++) {
				FavouritesStorage.removeFavourite(getActivity(), userUuid, selected.get(i));
			}
		}
		updateViews();
		getActivity().invalidateOptionsMenu();
	}
	
	private void updateViews() {
		favourites = FavouritesStorage.getFavourites(getActivity(), settings.getString(Settings.UUID));
		getActivity().invalidateOptionsMenu();
		if (Utilities.isEmpty(favourites)) {
			showLayout(empty);
		} else {
			showLayout(list);
			adapter = new JobsAdapter(getActivity(), favourites);
			list.setAdapter(adapter);
		}
	}
	
	private void showLayout(View layout) {
		list.setVisibility(layout == list ? View.VISIBLE : View.INVISIBLE);
		empty.setVisibility(layout == empty ? View.VISIBLE : View.INVISIBLE);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.favourites_fragment_actions, menu);
		MenuItem emailItem = menu.findItem(R.id.action_email);
		if (emailItem != null) {
			emailItem.setVisible(!Utilities.isEmpty(favourites));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_email:
			emailJobs();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void emailJobs() {
		if (!Utilities.isEmpty(favourites) && DrillingJobsApp.isRegistered()) {
			sendEmail();
		} else {
			Intent intent = new Intent(getActivity(), RegisterDialogScreen.class);
			startActivityForResult(intent, DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE);
		}
	}
	
	private void sendEmail() {
		if (Utilities.isConnectionAvailable(getActivity())) {
			Intent intent = new Intent(getActivity(), ApiService.class);
			intent.setData(Uri.parse(ApiData.COMMAND_SEND_EMAIL));
			intent.setAction(ApiData.METHOD_POST);
			intent.putExtra(ApiData.UUIDS, Utilities.createUuids(favourites));
			intent.putExtra(ApiData.EMAIL, DrillingJobsApp.getEmailSetting().getEmail());
			intent.putExtra(ApiData.TRIGGER, "favorite");
			getActivity().startService(intent);
			showProgressDialog(R.string.sending_email);
		} else {
			showConnectionErrorDialog(); 
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Job job = favourites.get(position);
		job.setRead(true);
		FavouritesStorage.saveFavourites(getActivity(), settings.getString(Settings.UUID), favourites);
		dbStorage.updateJob(job);
		adapter.notifyDataSetChanged();
		
		Job j = DrillingJobsApp.getJobByUuid(job.getUuid());
		j.setRead(true);
		
		Activity activity = getActivity();
		if (activity != null) {
			((MainScreen) activity).setMenuAdapter();
		}
		
		Intent intent = new Intent(getActivity(), JobDetailsScreen.class);
		intent.putExtra(Job.UUID, job.getUuid());
		startActivityForResult(intent, DrillingJobsApp.VIEW_JOB_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DrillingJobsApp.VIEW_JOB_REQUEST_CODE) {
			updateViews();
		} else if (requestCode == DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				emailJobs();
			}
		}
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		((BaseScreen) getActivity()).hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			if (apiResponse != null) {
				String method = apiResponse.getMethod();
				String command = apiResponse.getRequestName();
				int statusCode = apiResponse.getStatus();
				if (ApiData.COMMAND_SEND_EMAIL.equalsIgnoreCase(command) && ApiData.METHOD_POST.equalsIgnoreCase(method)) {
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
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

}
