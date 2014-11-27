package com.wds.oilfieldDrillingJobs.screen;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.wds.oilfieldDrillingJobs.R;

public class OtherAppsScreen extends BaseScreen implements OnClickListener {
	
	private View app1Btn;
	private View app2Btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_apps_screen);
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void initializeViews() {
		app1Btn = findViewById(R.id.app1Btn);
		app1Btn.setOnClickListener(this);
		
		app2Btn = findViewById(R.id.app2Btn);
		app2Btn.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.app1Btn || id == R.id.app2Btn) {
			String link = (String) v.getTag();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(link));
			startActivity(Intent.createChooser(intent, getString(R.string.open_via)));
		}
	}

}
