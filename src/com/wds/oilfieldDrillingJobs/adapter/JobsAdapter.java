package com.wds.oilfieldDrillingJobs.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.model.Job;

public class JobsAdapter extends BaseAdapter {
	
	private Context context;
	private List<Job> jobs;
	
	public JobsAdapter(Context context, List<Job> jobs) {
		this.context = context;
		this.jobs = jobs;
	}

	@Override
	public int getCount() {
		return jobs.size();
	}

	@Override
	public Job getItem(int position) {
		return jobs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.job_list_item, null);
		}
		
		Job job = getItem(position);
		
		ImageView jobUnreadMarker = (ImageView) convertView.findViewById(R.id.jobUnreadMarker);
		jobUnreadMarker.setVisibility(job.isRead() ? View.INVISIBLE : View.VISIBLE);
		
		TextView jobTitle = (TextView) convertView.findViewById(R.id.jobTitle);
		jobTitle.setText(job.getTitle());
		jobTitle.setTextColor(job.isRead() ? 0xff909090 : 0xff227cb6);
		
		TextView jobLocation = (TextView) convertView.findViewById(R.id.jobLocation);
		jobLocation.setText(job.getLocation());
		jobLocation.setTextColor(job.isRead() ? 0xff686868 : 0xff666666);
		jobLocation.setVisibility(job.hasLocation() ? View.VISIBLE : View.GONE);
		
		TextView jobDescription = (TextView) convertView.findViewById(R.id.jobDescription);
		jobDescription.setText(Html.fromHtml(job.getDescription()));
		jobDescription.setTextColor(job.isRead() ? 0xff444444 : 0xff020202);
		jobDescription.setVisibility(job.hasDescription() ? View.VISIBLE : View.GONE);
		
		return convertView;
	}

}
