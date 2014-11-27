package com.wds.oilfieldDrillingJobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;
import android.content.Context;

import com.wds.oilfieldDrillingJobs.model.EmailSetting;
import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.model.Subscription;
import com.wds.oilfieldDrillingJobs.storage.Settings;
import com.wds.oilfieldDrillingJobs.storage.UuidsStorage;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class DrillingJobsApp extends Application {
	
	public static final int REGISTER_EMAIL_REQUEST_CODE = 0;
	public static final int SETUP_NOTIFICATIONS_REQUEST_CODE = 1;
	public static final int VIEW_JOB_REQUEST_CODE = 2;
	
	private static List<Job> jobs;
	private static Object jobsLock = new Object();
	
	private static List<Subscription> subscriptions;
	private static Object subscriptionsLock = new Object();
	
	private static EmailSetting emailSetting;
	
	public static List<Job> getJobs() {
		synchronized (jobsLock) {
			return jobs;
		} 
	}
	
	public static Job getJobByPosition(int position) {
		synchronized (jobsLock) {
			return jobs.get(position);
		}
	}
	
	public static Job getJobByUuid(String uuid) {
		synchronized (jobsLock) {
			if (!Utilities.isEmpty(jobs)) {
				for (Job job : jobs) {
					if (job.hasUuid() && job.getUuid().equals(uuid)) {
						return job;
					}
				}
			}
			return null;
		}
	}
	
	public static int getJobPositionByUuid(String uuid) {
		Job job = getJobByUuid(uuid);
		return job != null ? jobs.indexOf(job) : -1;
	}
	
	public static void setJobs(List<Job> list) {
		synchronized (jobsLock) {
			jobs = list;
		}
	}
	
	public static List<Subscription> getSubscriptions() {
		synchronized (subscriptionsLock) {
			return subscriptions;
		}
	}
	
	public static Subscription getSubscriptionByUuid(String uuid) {
		synchronized (subscriptionsLock) {
			if (!Utilities.isEmpty(subscriptions)) {
				for (Subscription subscription : subscriptions) {
					if (subscription.hasUuid() && subscription.getUuid().equals(uuid)) {
						return subscription;
					}
				}
			}
			return null;
		}
	}
	
	public static void setSubscriptions(List<Subscription> list) {
		synchronized (subscriptionsLock) {
			subscriptions = list;
		}
	}
	
	public static boolean hasSubscriptions() {
		synchronized (subscriptionsLock) {
			return !Utilities.isEmpty(subscriptions);
		}
	}
	
	public static EmailSetting getEmailSetting() {
		return emailSetting;
	}
	
	public static void setEmailSetting(EmailSetting setting) {
		emailSetting = setting;
	}
	
	public static boolean isRegistered() {
		return emailSetting != null && emailSetting.hasEmail();
	}
	
	public static List<Job> getNotifications(Context context) {
		List<Job> jobs = getJobs();
		List<Subscription> subscriptions = getSubscriptions();
		if (Utilities.isEmpty(jobs) || Utilities.isEmpty(subscriptions)) {
			return null;
		}
		
		List<Job> result = new ArrayList<Job>();
		for (Job job : jobs) {
			boolean isNotification = false;
			for (Subscription subscription : subscriptions) {
				if (job.hasTitle()) {
					if ("title".equalsIgnoreCase(subscription.getSubType()) && job.hasTitle() &&
						job.getTitle().contains(subscription.getKeyword()) ||
						"location".equalsIgnoreCase(subscription.getSubType()) && job.hasLocation() &&
						job.getLocation().contains(subscription.getKeyword()))
					{
						isNotification = true;
						break;
					}
				}
			}
			if (isNotification) {
				result.add(job);
			}
		}
		
		Settings settings = new Settings(context);
		List<String> clearedUuids = UuidsStorage.getUuids(context, settings.getString(Settings.UUID));
		if (!Utilities.isEmpty(clearedUuids) && !Utilities.isEmpty(result)) {
			Iterator<Job> i = result.iterator();
			while (i.hasNext()) {
				Job job = i.next();
				boolean cleared = false;
				for (String uuid : clearedUuids) {
					if (uuid.equals(job.getUuid())) {
						cleared = true;
						break;
					}
				}
				if (cleared) {
					i.remove();
				}
			}
		}
		
		return result;
	}
	
	public static int getNotificationsUnreadCount(Context context) {
		List<Job> notifications = getNotifications(context);
		if (Utilities.isEmpty(notifications)) {
			return 0;
		} else {
			int unread = 0;
			for (Job notification : notifications) {
				if (!notification.isRead()) {
					unread++;
				}
			}
			return unread;
		}
	}

}
