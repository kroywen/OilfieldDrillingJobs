package com.wds.oilfieldDrillingJobs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.model.Job;

public class Utilities {
	
	public static final String yyyy_MM_ddTHH_mm_ss_S_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String yyyy_EEE_dd = "yyyy, EEE dd";
	public static final String dd_EEE_yyyy = "dd-EEE-yyyy";

	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}
	
	public static int dpToPx(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	public static String streamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}
	
	public static String convertDate(String date, String fromPattern, String toPattern) {
		DateFormat originalFormat = new SimpleDateFormat(fromPattern, Locale.US);
		DateFormat targetFormat = new SimpleDateFormat(toPattern, Locale.US);
		try {
			Date d = originalFormat.parse(date);
			return targetFormat.format(d); 
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}		
	}
	
	public static long parseDate(String date, String pattern) throws ParseException {
		DateFormat d = new SimpleDateFormat(pattern, Locale.US);
		return d.parse(date).getTime();
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String parseTime(long time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
		return sdf.format(new Date(time));
	}
	
	@SuppressWarnings("unchecked")
	public static String createUuids(Object object) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (object != null) {
			if (object instanceof Job) {
				sb.append('\"').append(((Job) object).getUuid()).append('\"');
			} else if (object instanceof List<?>){
				try {
					List<Job> jobs = (List<Job>) object;
					if (!Utilities.isEmpty(jobs)) {
						for (int i=0; i<jobs.size(); i++) {
							sb.append('\"').append(jobs.get(i).getUuid()).append('\"');
							if (i < jobs.size() - 1) {
								sb.append(',');
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		sb.append(']');
		return sb.toString();
	} 
	
	public static List<String> getUuids(List<Job> jobs) {
		if (isEmpty(jobs)) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		for (Job job : jobs) {
			result.add(job.getUuid());
		}
		return result;
	}
	
	public static String timeAgo(Context context, long timeMillis) {
		long now = System.currentTimeMillis();
		if (now < timeMillis) {
			return null;
		}
		double deltaSeconds = (now - timeMillis) / 1000.0d;
		double deltaMinutes = deltaSeconds / 60;
//		int minutes;
		
		if (deltaSeconds < 5) {
			return context.getString(R.string.just_now);
		} else if (deltaSeconds < 60) {
			return context.getString(R.string.few_seconds_ago);
		} else if (deltaSeconds < 120) {
			return context.getString(R.string.minute_ago);
		} else if (deltaMinutes < 60) {
			return context.getString(R.string.few_minutes_ago);
		} else if (deltaMinutes < 120) {
			return context.getString(R.string.hour_ago);
		} else if (deltaMinutes < 24 * 60) {
//			minutes = (int) Math.floor(deltaMinutes / 60);
			return context.getString(R.string.few_hours_ago);
		} else if (deltaMinutes < 24 * 60 * 2) {
			return context.getString(R.string.yesterday);
		} else if (deltaMinutes < 24 * 60 * 7) {
//			minutes = (int) Math.floor(deltaMinutes / (60 * 24));
			return context.getString(R.string.few_days_ago);
		} else if (deltaMinutes < 24 * 60 * 14) {
			return context.getString(R.string.last_week);
		} else if (deltaMinutes < 24 * 60 * 31) {
//			minutes = (int) Math.floor(deltaMinutes / (60 * 24 * 7));
			return context.getString(R.string.few_weeks_ago);
		} else if (deltaMinutes < 24 * 60 * 61) {
			return context.getString(R.string.last_month);
		} else if (deltaMinutes < 24 * 60 * 365.25) {
//			minutes = (int) Math.floor(deltaMinutes / (60 * 24 * 30));
			return context.getString(R.string.few_month_ago);
		} else if (deltaMinutes < 24 * 60 * 731) {
			return context.getString(R.string.last_year);
		}
		
//		minutes = (int) Math.floor(deltaMinutes / (60 * 24 * 365));
	    return context.getString(R.string.few_years_ago);
	}
	
	public static String dateTimeAgo(Context context, long timeMillis) {
		long now = System.currentTimeMillis();
		if (now < timeMillis) {
			return null;
		}
		double deltaSeconds = (now - timeMillis) / 1000.0d;
		double deltaMinutes = deltaSeconds / 60;
//		int minutes;
		
		if (deltaMinutes < 24 * 60) {
			return context.getString(R.string.today);
		} else if (deltaMinutes < 24 * 60 * 2) {
			return context.getString(R.string.yesterday);
		} else if (deltaMinutes < 24 * 60 * 7) {
//			minutes = (int) Math.floor(deltaMinutes / (60 * 24));
			return context.getString(R.string.few_days_ago);
		} else if (deltaMinutes < 24 * 60 * 14) {
			return context.getString(R.string.last_week);
		} else if (deltaMinutes < 24 * 60 * 31) {
//			minutes = (int) Math.floor(deltaMinutes / (60 * 24 * 7));
			return context.getString(R.string.few_weeks_ago);
		} else if (deltaMinutes < 24 * 60 * 61) {
			return context.getString(R.string.last_month);
		} else if (deltaMinutes < 24 * 60 * 365.25) {
//			minutes = (int) Math.floor(deltaMinutes / (60 * 24 * 30));
			return context.getString(R.string.few_month_ago);
		} else if (deltaMinutes < 24 * 60 * 731) {
			return context.getString(R.string.last_year);
		}
		
//		minutes = (int) Math.floor(deltaMinutes / (60 * 24 * 365));
	    return context.getString(R.string.few_years_ago);
	}

}

