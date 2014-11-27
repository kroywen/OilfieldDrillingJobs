package com.wds.oilfieldDrillingJobs.screen;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.wds.oilfieldDrillingJobs.R;

public class TermsScreen extends BaseScreen {
	
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_screen);
		initializeViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		webView.loadUrl("file:///android_asset/Terms_of_Use.html");
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
