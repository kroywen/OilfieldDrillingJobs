package com.wds.oilfieldDrillingJobs.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.wds.oilfieldDrillingJobs.api.ApiData;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class Job implements Serializable, Comparable<Job> {
	
	private static final long serialVersionUID = 7032332471586586524L;
	
	public static final String UUID = "uuid";
	public static final String TITLE = "name";
	public static final String LOCATION = "location";
	public static final String DESCRIPTION = "description";
	public static final String LINK = "link";
	public static final String JOB_TYPE = "job_type";
	public static final String SEQUENCE = "sequence";
	public static final String CRAWL_SOURCE = "crawl_source";
	public static final String CRAWL_TIME = "crawl_time";
	public static final String CRAWL_NUM = "crawl_num";
	public static final String CREATED_AT = "created_at";
	public static final String UPDATED_AT = "updated_at";
	public static final String UPDATED_AT_MILLIS = "updated_at_millis";
	public static final String READ = "read";

	private String uuid; // using strings as unique ids is bad practice
	private String title;
	private String location;
	private String description; // contains html tags
	private String link;
	private String jobType; // always "Drilling"
	private int sequence; // it controls job's order in list
	private String crawlSource; 
	private String crawlTime;
	private int crawlNum;
	private String createdAt;
	private String updatedAt;
	
	// local properties
	private long updatedAtMillis;
	private boolean read;
	
	public Job(String uuid, String title, String location, String description,
			String link, String jobType, int sequence, String crawlSource,
			String crawlTime, int crawlNum, String createdAt, String updatedAt,
			long updatedAtMillis, boolean read) 
	{
		this.uuid = uuid;
		this.title = title;
		this.location = location;
		this.description = description;
		this.link = link;
		this.jobType = jobType;
		this.sequence = sequence;
		this.crawlSource = crawlSource;
		this.crawlTime = crawlTime;
		this.crawlNum = crawlNum;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.updatedAtMillis = updatedAtMillis;
		this.read = read;
	}
	
	public Job(String uuid, String title, String location, String description, String createdAt, String updatedAt) 
	{
		this.uuid = uuid;
		this.title = title;
		this.location = location;
		this.description = description;
		this.jobType = "Drilling";
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		parseUpdatedAtTime();
	}

	public Job(JSONObject obj) {
		uuid = obj.optString(ApiData.UUID);
		title = obj.optString(ApiData.TITLE);
		location = obj.optString(ApiData.LOCATION);
		description = obj.optString(ApiData.DESCRIPTION);
		link = obj.optString(ApiData.LINK);
		jobType = obj.optString(ApiData.JOB_TYPE);
		sequence = obj.optInt(ApiData.SEQUENCE);
		crawlSource = obj.optString(ApiData.CRAWL_SOURCE);
		crawlTime = obj.optString(ApiData.CRAWL_TIME);
		crawlNum = obj.optInt(ApiData.CRAWL_NUM);
		createdAt = obj.optString(ApiData.CREATED_AT);
		updatedAt = obj.optString(ApiData.UPDATED_AT);
		
		read = false;
		parseUpdatedAtTime();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public boolean hasUuid() {
		return !TextUtils.isEmpty(uuid);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean hasTitle() {
		return !TextUtils.isEmpty(title);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean hasLocation() {
		return !TextUtils.isEmpty(location);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean hasDescription() {
		return !TextUtils.isEmpty(description);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public boolean hasLink() {
		return !TextUtils.isEmpty(link);
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getCrawlSource() {
		return crawlSource;
	}

	public void setCrawlSource(String crawlSource) {
		this.crawlSource = crawlSource;
	}

	public String getCrawlTime() {
		return crawlTime;
	}

	public void setCrawlTime(String crawlTime) {
		this.crawlTime = crawlTime;
	}

	public int getCrawlNum() {
		return crawlNum;
	}

	public void setCrawlNum(int crawlNum) {
		this.crawlNum = crawlNum;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public long getUpdatedAtMillis() {
		return updatedAtMillis;
	}
	
	public void setUpdatedAtMillis(long updatedAtMillis) {
		this.updatedAtMillis = updatedAtMillis;
	}
	
	public boolean isRead() {
		return read;
	}
	
	public void setRead(boolean read) {
		this.read = read;
	}
	
	private void parseUpdatedAtTime() {
		try {
			updatedAtMillis = Utilities.parseDate(updatedAt, Utilities.yyyy_MM_ddTHH_mm_ss_S_Z);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(Job another) {
		return sequence > another.getSequence() ? 1 :
			sequence < another.getSequence() ? -1 : 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Job) {
			Job another = (Job) o;
			return uuid.equals(another.uuid);
		} else {
			return false;
		}
	}
	
	public static Comparator<Map.Entry<String, List<Job>>> comparator = new Comparator<Map.Entry<String, List<Job>>>() {
		@Override
		public int compare(Map.Entry<String, List<Job>> o1, Map.Entry<String, List<Job>> o2) {
			String key1 = o1.getKey();
			String key2 = o2.getKey();
			if (key1.equals("Today") ||
				key1.equals("Yesterday") && !key2.equals("Today") ||
				key1.equals("Few days ago") && !key2.equals("Yesterday") && !key2.equals("Today") ||
				key1.equals("Last week") && !key2.equals("Few days ago") && !key2.equals("Yesterday") && !key2.equals("Today") ||
				key1.equals("Few weeks ago") && !key2.equals("Last week") && !key2.equals("Few days ago") && !key2.equals("Yesterday") && !key2.equals("Today") ||
				key1.equals("Last month") && !key2.equals("Few weeks ago") && !key2.equals("Last week") && !key2.equals("Few days ago") && !key2.equals("Yesterday") && !key2.equals("Today") ||
				key1.equals("Few month ago") && !key2.equals("Last month") && !key2.equals("Few weeks ago") && !key2.equals("Last week") && !key2.equals("Few days ago") && !key2.equals("Yesterday") && !key2.equals("Today") ||
				key1.equals("Last year") && !key2.equals("Few month ago") && !key2.equals("Last month") && !key2.equals("Few weeks ago") && !key2.equals("Last week") && !key2.equals("Few days ago") && !key2.equals("Yesterday") && !key2.equals("Today"))
			{
				return -1;
			} else {
				return 1; 
			}
		}
	};

}
