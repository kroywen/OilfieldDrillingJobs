package com.wds.oilfieldDrillingJobs.screen;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.wds.oilfieldDrillingJobs.R;

public class AboutScreen extends BaseScreen implements OnClickListener {
	
	private View aboutAppBtn;
	private View termsBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_screen);
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
	
	private void initializeViews() {
		aboutAppBtn = findViewById(R.id.aboutAppBtn);
		aboutAppBtn.setOnClickListener(this);
		
		termsBtn = findViewById(R.id.termsBtn);
		termsBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.aboutAppBtn:
			Intent intent = new Intent(this, AboutAppScreen.class);
			startActivity(intent);
			break;
		case R.id.termsBtn:
			intent = new Intent(this, TermsScreen.class);
			startActivity(intent);
			break;
		}
	}

}
