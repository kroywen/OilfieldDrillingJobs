package com.wds.oilfieldDrillingJobs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wds.oilfieldDrillingJobs.DrillingJobsApp;
import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.storage.DatabaseStorage;

public class MenuAdapter extends BaseAdapter {
	
	private Context context;
	private String[] items;
	private int[] icons = {
		R.drawable.icon_jobs,
		R.drawable.icon_notifications,
		R.drawable.icon_favourites,
		R.drawable.icon_settings
	};
	private DatabaseStorage dbStorage;
	
	public MenuAdapter(Context context) {
		this.context = context;
		this.items = context.getResources().getStringArray(R.array.menu_items);
		dbStorage = DatabaseStorage.getInstance(context);
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public String getItem(int position) {
		return items[position];
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
			convertView = inflater.inflate(R.layout.menu_list_item, null);
		}
		
		String item = items[position];
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		icon.setImageResource(icons[position]);
		
		TextView textView = (TextView) convertView.findViewById(R.id.text);
		textView.setText(item);
		
		TextView unread = (TextView) convertView.findViewById(R.id.unread);
		if (position == 0) {
			int unreadCount = dbStorage.getUnreadJobsCount();
			unread.setText(String.valueOf(unreadCount));
			unread.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
		} else if (position == 1) {
			int unreadCount = DrillingJobsApp.getNotificationsUnreadCount(context);
			unread.setText(String.valueOf(unreadCount));
			unread.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
		} else {
			unread.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
