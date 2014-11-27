package com.wds.oilfieldDrillingJobs.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.screen.BaseScreen;
import com.wds.oilfieldDrillingJobs.screen.JobDetailsScreen;
import com.wds.oilfieldDrillingJobs.screen.RegisterDialogScreen;
import com.wds.oilfieldDrillingJobs.storage.DatabaseStorage;
import com.wds.oilfieldDrillingJobs.storage.FavouritesStorage;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class JobDetailsFragment extends Fragment implements OnClickListener {
	
	public static final int ACTION_NONE = 0;
	public static final int ACTION_EMAIL = 1;
	public static final int ACTION_FAVOURITE = 2;
	
	private TextView jobTitle;
	private TextView jobLocation;
	private TextView jobUpdatedAt;
	private TextView jobDescription;
	private View linkBtn;
	private View emailBtn;
	private View shareBtn;
	private ImageView favouriteBtn;
	
	private DatabaseStorage dbStorage;
	private Settings settings;
	private String uuid;
	private Job job;
	private int action;
	
	public static JobDetailsFragment newInstance(String uuid) {
		JobDetailsFragment f = new JobDetailsFragment();
		Bundle args = new Bundle();
		args.putString(Job.UUID, uuid);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbStorage = DatabaseStorage.getInstance(getActivity());
		settings = new Settings(getActivity());
		
		Bundle args = getArguments();
		if (args != null && args.containsKey(Job.UUID)) {
			uuid = args.getString(Job.UUID);
			job = dbStorage.getJobByUuid(uuid);
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.job_details_fragment, null);
		initializeViews(view);
		updateViews();
		return view;
	}
	
	private void initializeViews(View view) {
		jobTitle = (TextView) view.findViewById(R.id.jobTitle);
		jobLocation = (TextView) view.findViewById(R.id.jobLocation);
		jobUpdatedAt = (TextView) view.findViewById(R.id.jobUpdatedAt);
		jobDescription = (TextView) view.findViewById(R.id.jobDescription);
		emailBtn = view.findViewById(R.id.emailBtn);
		emailBtn.setOnClickListener(this);
		shareBtn = view.findViewById(R.id.shareBtn);
		shareBtn.setOnClickListener(this);
		favouriteBtn = (ImageView) view.findViewById(R.id.favouriteBtn);
		favouriteBtn.setOnClickListener(this);
		linkBtn = view.findViewById(R.id.linkBtn);
		linkBtn.setOnClickListener(this);
	}
	
	private void updateViews() {
		if (job != null) {
			jobTitle.setText(job.getTitle());
			
			jobLocation.setText(job.getLocation());
			jobLocation.setVisibility(job.hasLocation() ? View.VISIBLE : View.GONE);
			
			String updatedAt = Utilities.parseTime(job.getUpdatedAtMillis(), Utilities.dd_EEE_yyyy);
			jobUpdatedAt.setText(updatedAt);
			
			jobDescription.setText(Html.fromHtml(job.getDescription()));
			
			linkBtn.setVisibility(job.hasLink() ? View.VISIBLE : View.GONE);
			
			
			String userUuid = settings.getString(Settings.UUID);
			int imageResId = FavouritesStorage.isFavourite(getActivity(), userUuid, job) ? 
				R.drawable.icon_favourite_on : R.drawable.icon_favourite_off;
			favouriteBtn.setImageResource(imageResId);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emailBtn:
			emailJob();
			break;
		case R.id.shareBtn:
			shareJob();
			break;
		case R.id.favouriteBtn:
			toggleFavouriteJob();
			break;
		case R.id.linkBtn:
			openJob();
			break;	
		}
	}
	
	private void emailJob() {
		if (job != null && DrillingJobsApp.isRegistered()) {
			try {
				String uuids = Utilities.createUuids(job);
				((JobDetailsScreen) getActivity()).sendEmail(uuids);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			action = ACTION_EMAIL;
			Intent intent = new Intent(getActivity(), RegisterDialogScreen.class);
			startActivityForResult(intent, DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				if (action == ACTION_EMAIL) {
					emailJob(); 
				} else if (action == ACTION_FAVOURITE) {
					toggleFavouriteJob();
				}
			} else {
				if (data != null && data.hasExtra("error")) {
					String error = data.getStringExtra("error");
					((BaseScreen) getActivity()).showInfoDialog(getString(R.string.error), error);
				}
			}
		}
	}
	
	private void shareJob() {
		if (job != null) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			String extraText = getString(R.string.share_job_pattern, job.getLink());
			intent.putExtra(Intent.EXTRA_TEXT, extraText);
			intent.setType("text/plain");
			startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
		}
	}
	
	private void toggleFavouriteJob() {
		if (DrillingJobsApp.isRegistered()) {
			if (job != null) {
				String userUuid = settings.getString(Settings.UUID);
				int imageResId = 0;
				if (FavouritesStorage.isFavourite(getActivity(), userUuid, job)) {
					FavouritesStorage.removeFavourite(getActivity(), userUuid, job);
					imageResId = R.drawable.icon_favourite_off;
				} else {
					FavouritesStorage.addFavourite(getActivity(), userUuid, job);
					imageResId = R.drawable.icon_favourite_on;
				}
				favouriteBtn.setImageResource(imageResId);
			}
		} else {
			action = ACTION_FAVOURITE;
			Intent intent = new Intent(getActivity(), RegisterDialogScreen.class);
			startActivityForResult(intent, DrillingJobsApp.REGISTER_EMAIL_REQUEST_CODE);
		}
	}
	
	private void openJob() {
		if (job != null && job.hasLink()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(job.getLink().startsWith("http") ? job.getLink() : "http://" + job.getLink()));
			startActivity(Intent.createChooser(intent, getString(R.string.open_via)));
		}
	}

}
