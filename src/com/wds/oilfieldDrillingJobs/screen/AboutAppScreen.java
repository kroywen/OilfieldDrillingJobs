package com.wds.oilfieldDrillingJobs.screen;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.wds.oilfieldDrillingJobs.R;

public class AboutAppScreen extends BaseScreen {
	
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_app_screen);
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		webView.loadUrl("file:///android_asset/About_App.html");
	}
	
	private void initializeViews() {
		webView = (WebView) findViewById(R.id.webView); 
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

}
