package com.wds.oilfieldDrillingJobs.storage;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class DatabaseStorage {
	
	protected static DatabaseStorage instance;
	protected Context context;
	protected DatabaseHelper dbHelper;
	protected SQLiteDatabase db;
	
	protected DatabaseStorage(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public static DatabaseStorage getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseStorage(context);
		return instance;
	}
	
	/************************************************************************************************************************
	 ******************************************************* J O B S ******************************************************** 
	 ************************************************************************************************************************/
	
	public synchronized List<Job> getJobs() {
		List<Job> jobs = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_JOBS, null, null, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				jobs = new LinkedList<Job>();
				do {
					Job job = new Job(
						c.getString(c.getColumnIndex(Job.UUID)),
						c.getString(c.getColumnIndex(Job.TITLE)),
						c.getString(c.getColumnIndex(Job.LOCATION)),
						c.getString(c.getColumnIndex(Job.DESCRIPTION)),
						c.getString(c.getColumnIndex(Job.LINK)),
						c.getString(c.getColumnIndex(Job.JOB_TYPE)),
						c.getInt(c.getColumnIndex(Job.SEQUENCE)),
						c.getString(c.getColumnIndex(Job.CRAWL_SOURCE)),
						c.getString(c.getColumnIndex(Job.CRAWL_TIME)),
						c.getInt(c.getColumnIndex(Job.CRAWL_NUM)),
						c.getString(c.getColumnIndex(Job.CREATED_AT)),
						c.getString(c.getColumnIndex(Job.UPDATED_AT)),
						c.getLong(c.getColumnIndex(Job.UPDATED_AT_MILLIS)),
						c.getInt(c.getColumnIndex(Job.READ)) == 1
					);
					jobs.add(job);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs;
	}
	
	public Job getJobByUuid(String uuid) {
		Job job = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_JOBS, null, Job.UUID+"='"+uuid+"'", null, null, null, null);
			if (c != null && c.moveToFirst()) {
				job = new Job(
					uuid,
					c.getString(c.getColumnIndex(Job.TITLE)),
					c.getString(c.getColumnIndex(Job.LOCATION)),
					c.getString(c.getColumnIndex(Job.DESCRIPTION)),
					c.getString(c.getColumnIndex(Job.LINK)),
					c.getString(c.getColumnIndex(Job.JOB_TYPE)),
					c.getInt(c.getColumnIndex(Job.SEQUENCE)),
					c.getString(c.getColumnIndex(Job.CRAWL_SOURCE)),
					c.getString(c.getColumnIndex(Job.CRAWL_TIME)),
					c.getInt(c.getColumnIndex(Job.CRAWL_NUM)),
					c.getString(c.getColumnIndex(Job.CREATED_AT)),
					c.getString(c.getColumnIndex(Job.UPDATED_AT)),
					c.getLong(c.getColumnIndex(Job.UPDATED_AT_MILLIS)),
					c.getInt(c.getColumnIndex(Job.READ)) == 1
				);
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return job;
	}
	
	public boolean insertJob(Job job) {
		if (job == null)
			return false;
		ContentValues values = prepareJobContentValues(job);
		long insertedId = db.insert(DatabaseHelper.TABLE_JOBS, null, values);
		return insertedId != -1;
	}
	
	public boolean updateJob(Job job) {
		if (job == null)
			return false;
		ContentValues values = prepareJobContentValues(job);
		int rowsAffected = db.update(DatabaseHelper.TABLE_JOBS, values, Job.UUID+"='"+job.getUuid()+"'", null);
		return rowsAffected == 1;
	}
	
	protected ContentValues prepareJobContentValues(Job job) {
		ContentValues values = new ContentValues();
		values.put(Job.UUID, job.getUuid());
		values.put(Job.TITLE, job.getTitle());
		values.put(Job.LOCATION, job.getLocation());
		values.put(Job.DESCRIPTION, job.getDescription());
		values.put(Job.LINK, job.getLink());
		values.put(Job.JOB_TYPE, job.getJobType());
		values.put(Job.SEQUENCE, job.getSequence());
		values.put(Job.CRAWL_SOURCE, job.getCrawlSource());
		values.put(Job.CRAWL_TIME, job.getCrawlTime());
		values.put(Job.CRAWL_NUM, job.getCrawlNum());
		values.put(Job.CREATED_AT, job.getCreatedAt());
		values.put(Job.UPDATED_AT, job.getUpdatedAt());
		values.put(Job.UPDATED_AT_MILLIS, job.getUpdatedAtMillis());
		values.put(Job.READ, job.isRead() ? 1 : 0);
		return values;
	}
	
	public boolean deleteJob(Job job) {
		if (job == null)
			return false;
		int rowsAffected = db.delete(DatabaseHelper.TABLE_JOBS, Job.UUID+"='"+job.getUuid()+"'", null);
		return rowsAffected == 1;
	}
	
	public void synchronizeJobs(List<Job> jobs) {
		if (Utilities.isEmpty(jobs)) {
			return;
		}
		try {
			db.beginTransaction();
			for (Job job : jobs) {
				Job local = getJobByUuid(job.getUuid());
				if (local == null) {
					insertJob(job);
				} else {
					updateJob(job);
				}
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getUnreadJobsCount() {
		List<Job> jobs = getJobs();
		if (Utilities.isEmpty(jobs)) {
			return 0;
		} else {
			int count = 0;
			for (Job job : jobs) {
				if (!job.isRead()) {
					count++;
				}
			}
			return count;
		}
	}

}
