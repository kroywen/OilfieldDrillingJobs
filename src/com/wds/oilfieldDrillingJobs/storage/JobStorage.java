package com.wds.oilfieldDrillingJobs.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class JobStorage {
	
	public static final String FILENAME = "DrillingJobsHistory";
	
	private Context context;
	
	private JobStorage(Context context) {
		this.context = context;
	}
	
	public static JobStorage newInstance(Context context) {
		return new JobStorage(context);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<Job> getJobs() {
		try {
			FileInputStream fis = context.openFileInput(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Job> chat = (List<Job>) ois.readObject();
			ois.close();
			return chat;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized void addJobs(List<Job> jobs) {
		List<Job> prevJobs = getJobs();
		List<Job> newJobs = new ArrayList<Job>();
		if (!Utilities.isEmpty(prevJobs)) {
			newJobs.addAll(prevJobs);
		}
		if (!Utilities.isEmpty(jobs)) { 
			if (!newJobs.isEmpty()) { 
				for (Job job : jobs) {
					boolean found = false;
					for (Job oldJob : newJobs) {
						if (job.equals(oldJob)) {
							found = true;
							break;
						}
					}
					if (!found) {
						newJobs.add(job);
					}
				}
			} else {
				newJobs.addAll(jobs);
			}
		}
		saveJobs(newJobs);
	}
	
	public synchronized void saveJobs(List<Job> jobs) {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(jobs);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setJobRead(Job job) {
		if (job == null) {
			return;
		}
		List<Job> jobs = getJobs();
		if (!Utilities.isEmpty(jobs)) {
			for (Job job1 : jobs) {
				if (job1.equals(job)) {
					job1.setRead(true);
					break;
				}
			}
		}
		saveJobs(jobs);
	}
	
	public synchronized int getUnreadJobsCount() {
		int count = 0;
		List<Job> jobs = getJobs();
		if (!Utilities.isEmpty(jobs)) {
			for (Job job : jobs) {
				if (!job.isRead()) {
					count++;
				}
			}
		}
		return count;
	}

}
