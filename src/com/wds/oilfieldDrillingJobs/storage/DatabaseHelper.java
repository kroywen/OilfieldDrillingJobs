package com.wds.oilfieldDrillingJobs.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wds.oilfieldDrillingJobs.model.Job;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "oilfield_drilling_jobs";
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_JOBS = "jobs";
	
	public static final String CREATE_TABLE_JOBS =
			"create table if not exists " + TABLE_JOBS + " (" +
			Job.UUID + " text primary key, " +
			Job.TITLE + " text, " +
			Job.LOCATION + " text, " +
			Job.DESCRIPTION + " text, " +
			Job.LINK + " text, " +
			Job.JOB_TYPE + " text, " +
			Job.SEQUENCE + " integer, " +
			Job.CRAWL_SOURCE + " text, " + 
			Job.CRAWL_TIME + " text, " +
			Job.CRAWL_NUM + " integer, " +
			Job.CREATED_AT + " text, " +
			Job.UPDATED_AT + " text, " +
			Job.UPDATED_AT_MILLIS + " integer, " +
			Job.READ + " integer);";
	
	public static final String DROP_TABLE_JOBS =
			"drop table if exists " + TABLE_JOBS;
	
	protected Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		if (db != null) {
			db.execSQL(CREATE_TABLE_JOBS);
		}
	}
	
	protected void dropTables(SQLiteDatabase db) {
		if (db != null) {
			db.execSQL(DROP_TABLE_JOBS);
		}
	}

}
