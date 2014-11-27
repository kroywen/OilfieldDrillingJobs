package com.wds.oilfieldDrillingJobs.screen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.api.ApiResponse;
import com.wds.oilfieldDrillingJobs.api.ApiService;
import com.wds.oilfieldDrillingJobs.dialog.ConfirmationDialog;
import com.wds.oilfieldDrillingJobs.dialog.InputDialog;
import com.wds.oilfieldDrillingJobs.dialog.InputDialog.OnInputClickListener;
import com.wds.oilfieldDrillingJobs.model.Subscription;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class NotificationSetupScreen extends BaseScreen implements OnClickListener {
	
	public static final int MODE_NORMAL = 0;
	public static final int MODE_EDIT = 1;
	
	private View addPositionBtn;
	private View positionInfoBtn;
	private LinearLayout positionContent;
	private View addLocationBtn;
	private View locationInfoBtn;
	private LinearLayout locationContent;
	
	private List<Subscription> subscriptionsPosition;
	private List<Subscription> subscriptionsLocation;
	private int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_setup_screen);
		initializeViews();
		updateViews();
		
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		if (!DrillingJobsApp.hasSubscriptions()) {
			if (Utilities.isConnectionAvailable(this)) {
				loadSubscriptions();
			} else {
				showConnectionErrorDialog();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.notifications_setup_screen_actions, menu);
	    MenuItem editItem = menu.findItem(R.id.action_edit);
	    int iconRes = mode == MODE_EDIT ? R.drawable.ic_menu_done : R.drawable.ic_menu_edit;
	    boolean visible = !(Utilities.isEmpty(subscriptionsLocation) && Utilities.isEmpty(subscriptionsPosition));
	    editItem.setIcon(iconRes).setVisible(visible);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			if (Utilities.isEmpty(subscriptionsLocation) && Utilities.isEmpty(subscriptionsPosition)) {
				if (mode == MODE_EDIT) {
					toggleMode();
					return true;
				} else {
					return super.onOptionsItemSelected(item);
				}
			} else {
				toggleMode();
				return true;
			}
		case android.R.id.home:
			if (mode == MODE_EDIT) {
				toggleMode();
			} else {
				finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mode == MODE_EDIT) {
			toggleMode();
		} else {
			super.onBackPressed();
		}
	}
	
	private void toggleMode() {
		mode = mode == MODE_EDIT ? MODE_NORMAL : MODE_EDIT;
		updateModeUI();
	}
	
	private void updateModeUI() {
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(mode == MODE_EDIT ? R.string.edit : R.string.notification_setup);
		
		invalidateOptionsMenu();
		
		updateJobListUi(locationContent, subscriptionsLocation);
		updateJobListUi(positionContent, subscriptionsPosition);
	}
	
	private void updateJobListUi(LinearLayout layout, List<Subscription> list) {
		if (!Utilities.isEmpty(list)) {
			for (int i=0; i<layout.getChildCount(); i++) {
				View child = layout.getChildAt(i);
				if (child != null) {
					View deleteView = child.findViewById(R.id.deleteBtn);
					if (deleteView != null) {
						try {
							ImageView deleteBtn = (ImageView) deleteView;
							deleteBtn.setVisibility(mode == MODE_EDIT ? View.VISIBLE : View.GONE);
						} catch (ClassCastException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private void initializeViews() {
		addPositionBtn = findViewById(R.id.addPositionBtn);
		addPositionBtn.setOnClickListener(this);
		positionInfoBtn = findViewById(R.id.positionInfoBtn);
		positionInfoBtn.setOnClickListener(this);
		positionContent = (LinearLayout) findViewById(R.id.positionContent);
		addLocationBtn = findViewById(R.id.addLocationBtn);
		addLocationBtn.setOnClickListener(this);
		locationInfoBtn = findViewById(R.id.locationInfoBtn);
		locationInfoBtn.setOnClickListener(this);
		locationContent = (LinearLayout) findViewById(R.id.locationContent);
	}
	
	private void updateViews() {
		List<Subscription> all = DrillingJobsApp.getSubscriptions();
		subscriptionsPosition = filterSubscriptionsByType(all, "title");
		populatePositionContent();
		subscriptionsLocation = filterSubscriptionsByType(all, "location");
		populateLocationContent();
		invalidateOptionsMenu();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addPositionBtn:
			if (!Utilities.isEmpty(subscriptionsPosition) && subscriptionsPosition.size() == 10) {
				showInfoDialog(R.string.information, R.string.job_position_max_limit_reached);
			} else {
				showAddJobPositionDialog();
			}
			break;
		case R.id.positionInfoBtn:
			showInfoDialog(R.string.whats_this, R.string.position_info_text);
			break;
		case R.id.addLocationBtn:
			if (!Utilities.isEmpty(subscriptionsLocation) && subscriptionsLocation.size() == 10) {
				showInfoDialog(R.string.information, R.string.job_location_max_limit_reached);
			} else {
				showAddJobLocationDialog();
			}
			break;
		case R.id.locationInfoBtn:
			showInfoDialog(R.string.whats_this, R.string.location_info_text);
			break;
		}
	}
	
	private void showAddJobPositionDialog() {
		final InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.job_position));
		dialog.setText(getString(R.string.please_enter_position_keyword));
		dialog.setButtons(getString(R.string.ok), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				dialog.dismiss();
				if (Utilities.isConnectionAvailable(NotificationSetupScreen.this)) {
					addSubscription(inputText, "title");
				} else {
					showConnectionErrorDialog();
				}
			}
			@Override
			public void onInputCancelClick() {
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "AddJobPositionDialog");
	}
	
	private void showAddJobLocationDialog() {
		final InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.job_location));
		dialog.setText(getString(R.string.please_enter_location_keyword));
		dialog.setButtons(getString(R.string.ok), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				dialog.dismiss();
				if (Utilities.isConnectionAvailable(NotificationSetupScreen.this)) {
					addSubscription(inputText, "location");
				} else {
					showConnectionErrorDialog();
				}
			}
			@Override
			public void onInputCancelClick() {
				dialog.dismiss();
			}
		});
		dialog.show(getFragmentManager(), "AddJobLocationDialog");
	}
	
	private void addSubscription(String keyword, String subType) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.COMMAND_SUBSCRIPTIONS));
		intent.setAction(ApiData.METHOD_POST);
		intent.putExtra(ApiData.EMAIL, settings.getString(Settings.EMAIL));
		intent.putExtra(ApiData.KEYWORD, keyword);
		intent.putExtra(ApiData.SUB_TYPE, subType);
		startService(intent);
		int msgId = "title".equalsIgnoreCase(subType) ? R.string.adding_new_job_position : R.string.adding_new_job_location;
		showProgressDialog(msgId);
	}
	
	private void deleteSubscription(String uuid) {
		Subscription subscription = DrillingJobsApp.getSubscriptionByUuid(uuid);
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.COMMAND_SUBSCRIPTIONS_DELETE));
		intent.setAction(ApiData.METHOD_DELETE);
		intent.putExtra(ApiData.PARAM_ID, uuid);
		startService(intent);
		int msgId = "location".equalsIgnoreCase(subscription.getSubType()) ? R.string.deleting_job_location : R.string.deleting_job_position;
		showProgressDialog(msgId);
	}
	
	@SuppressLint("InflateParams")
	private void populateLocationContent() {
		locationContent.removeAllViews();
		if (Utilities.isEmpty(subscriptionsLocation)) {
			View fullDivider = getFullDivider();
			locationContent.addView(fullDivider);
		} else {
			View partDivider = getPartDivider();
			locationContent.addView(partDivider);
			for (int i=0; i<subscriptionsLocation.size(); i++) {
				Subscription subscription = subscriptionsLocation.get(i);
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View root = inflater.inflate(R.layout.subscription_item, null);
				
				TextView keywordView = (TextView) root.findViewById(R.id.keywordView);
				keywordView.setText(subscription.getKeyword());
				
				ImageView deleteBtn = (ImageView) root.findViewById(R.id.deleteBtn);
				deleteBtn.setVisibility(mode == MODE_EDIT ? View.VISIBLE : View.GONE);
				deleteBtn.setTag(subscription.getUuid());
				deleteBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String uuid = (String) v.getTag();
						if (!TextUtils.isEmpty(uuid)) {
							if (Utilities.isConnectionAvailable(NotificationSetupScreen.this)) {
								showConfirmDeleteSubscriptionDialog(uuid);
							} else {
								showConnectionErrorDialog();
							}
						}
					}
				});
				
				locationContent.addView(root);
				
				View divider = (i < subscriptionsLocation.size()-1) ? getPartDivider() : getFullDivider();
				locationContent.addView(divider);
			}
		}
	}
	
	@SuppressLint("InflateParams")
	private void populatePositionContent() {
		positionContent.removeAllViews();
		if (Utilities.isEmpty(subscriptionsPosition)) {
			View fullDivider = getFullDivider();
			positionContent.addView(fullDivider);
		} else {
			View partDivider = getPartDivider();
			positionContent.addView(partDivider);
			for (int i=0; i<subscriptionsPosition.size(); i++) {
				Subscription subscription = subscriptionsPosition.get(i);
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View root = inflater.inflate(R.layout.subscription_item, null);
				
				TextView keywordView = (TextView) root.findViewById(R.id.keywordView);
				keywordView.setText(subscription.getKeyword());
				
				ImageView deleteBtn = (ImageView) root.findViewById(R.id.deleteBtn);
				deleteBtn.setVisibility(mode == MODE_EDIT ? View.VISIBLE : View.GONE);
				deleteBtn.setTag(subscription.getUuid());
				deleteBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String uuid = (String) v.getTag();
						if (!TextUtils.isEmpty(uuid)) {
							if (Utilities.isConnectionAvailable(NotificationSetupScreen.this)) {
								showConfirmDeleteSubscriptionDialog(uuid);
							} else {
								showConnectionErrorDialog();
							}
						}
					}
				});
				
				positionContent.addView(root);
				
				View divider = (i < subscriptionsPosition.size()-1) ? getPartDivider() : getFullDivider();
				positionContent.addView(divider);
			}
		}
	}
	
	private void showConfirmDeleteSubscriptionDialog(final String uuid) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.confirmation));
		dialog.setText(getString(R.string.you_sure_want_delete_subscription));
		dialog.setOkListener(getString(R.string.ok), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				deleteSubscription(uuid);
			}
		});
		dialog.show(getFragmentManager(), "ClearNotificationsDialog");
	}
	
	private View getFullDivider() {
		View view = new View(this);
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1));
		view.setBackgroundColor(0xffcccccc);
		return view;
	}
	
	private View getPartDivider() {
		View view = new View(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
		int left = Utilities.dpToPx(this, 15);
		params.setMargins(left, 0, 0, 0);
		view.setLayoutParams(params);
		view.setBackgroundColor(0xffcccccc);
		return view;
	}

	private List<Subscription> filterSubscriptionsByType(List<Subscription> list, String type) {
		if (Utilities.isEmpty(list) || TextUtils.isEmpty(type)) {
			return list;
		}
		List<Subscription> result = new ArrayList<Subscription>();
		for (Subscription subscripiton : list) {
			if (subscripiton.getSubType().equalsIgnoreCase(type)) {
				result.add(subscripiton);
			}
		}
		return result;
	}
	
	private void loadSubscriptions() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.COMMAND_SUBSCRIPTIONS));
		intent.setAction(ApiData.METHOD_GET);
		intent.putExtra(ApiData.EMAIL, settings.getString(Settings.EMAIL));
		startService(intent);
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
					} else if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
						if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
							setResult(RESULT_OK);
							loadSubscriptions();
						}
					}
				} else if (ApiData.COMMAND_SUBSCRIPTIONS_DELETE.equalsIgnoreCase(command) &&
						   ApiData.METHOD_DELETE.equalsIgnoreCase(method)) 
				{
					if (statusCode == HttpStatus.SC_OK && !apiResponse.hasError()) {
						setResult(RESULT_OK);
						loadSubscriptions();
					}
				}
			}
		}
	}

}
