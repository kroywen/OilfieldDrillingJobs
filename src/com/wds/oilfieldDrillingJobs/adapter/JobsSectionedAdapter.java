package com.wds.oilfieldDrillingJobs.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class JobsSectionedAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Job> jobs;
	protected List<ListItem> items;
	
	public JobsSectionedAdapter(Context context, List<Job> jobs) {
		this.context = context;
		this.jobs = jobs;
		prepareItems();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public ListItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItem item = getItem(position);
		if (item.getType() == ListItem.TYPE_HEADER) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.header_list_item, null);

			TextView title = (TextView) convertView.findViewById(R.id.title);
			if (title != null) {
				title.setText(item.getTitle());
			}
			return convertView;
		}
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.job_list_item, null);
		
		Job job = item.getJob();
		
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
	
	private void prepareItems() {
		items = new ArrayList<ListItem>();
		if (Utilities.isEmpty(jobs)) {
			return;
		}
		
		Map<String, List<Job>> map = new TreeMap<String, List<Job>>();
		for (Job job : jobs) {
			String key = Utilities.dateTimeAgo(context, job.getUpdatedAtMillis());
			if (map.containsKey(key)) {
				List<Job> list = (List<Job>) map.get(key);
				list.add(job);
				map.put(key, list);
			} else {
				List<Job> list = new ArrayList<Job>();
				list.add(job);
				map.put(key, list);
			}
		}
		
		map = sortItems(map);
		
		Set<String> keys = map.keySet();
		Iterator<String> i = keys.iterator();
		while (i.hasNext()) {
			String key = i.next();
			List<Job> list = map.get(key);
			int count = Utilities.isEmpty(list) ? 0 : list.size();
			key = (count == 0) ? key : key + " (" + count + ")"; 
			items.add(new ListItem(key));
			
			if (!Utilities.isEmpty(list)) {
				for (Job job : list) {
					items.add(new ListItem(job));
				}
			}
		}
	}	
	
	private Map<String, List<Job>> sortItems(Map<String, List<Job>> map) {
		List<Map.Entry<String, List<Job>>> list = 
			new LinkedList<Map.Entry<String, List<Job>>>(map.entrySet());
 
		Collections.sort(list, Job.comparator);
 
		Map<String, List<Job>> sortedMap = new LinkedHashMap<String, List<Job>>();
		for (Iterator<Map.Entry<String, List<Job>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, List<Job>> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public class ListItem {
		
		public static final int TYPE_HEADER = 0;
		public static final int TYPE_JOB = 1;
		
		private String title;
		private Job job;
		private int type;
		
		public ListItem(Job job) {
			this(null, job, TYPE_JOB);
		}
		
		public ListItem(String title) {
			this(title, null, TYPE_HEADER);
		}
		
		public ListItem(String title, Job job, int type) {
			this.title = title;
			this.job = job;
			this.type = type;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public Job getJob() {
			return job;
		}
		
		public void setJob(Job job) {
			this.job = job;
		}
		
		public int getType() {
			return type;
		}
		
		public void setType(int type) {
			this.type = type;
		}
		
	}

}
