package com.wds.oilfieldDrillingJobs.screen;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wds.oilfieldDrillingJobs.R;
import com.wds.oilfieldDrillingJobs.adapter.MenuAdapter;
import com.wds.oilfieldDrillingJobs.fragment.BaseFragment;
import com.wds.oilfieldDrillingJobs.fragment.FavouritesFragment;
import com.wds.oilfieldDrillingJobs.fragment.JobsFragment;
import com.wds.oilfieldDrillingJobs.fragment.NotificationFragment;
import com.wds.oilfieldDrillingJobs.fragment.SettingsFragment;

public class MainScreen extends BaseScreen implements OnItemClickListener {
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private int selected = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setOnItemClickListener(this);
		setMenuAdapter();
				
		mDrawerToggle = new ActionBarDrawerToggle(
			MainScreen.this, mDrawerLayout, R.drawable.ic_drawer, 0, 0)
		{
	        public void onDrawerClosed(View view) {
	        	invalidateOptionsMenu();
	        }
	        public void onDrawerOpened(View drawerView) {
	        	invalidateOptionsMenu();
	        }
	    };
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	    
	    if (savedInstanceState == null) {
            selectItem(0);
        }
	}
	
	public void setMenuAdapter() {
		mDrawerList.setAdapter(new MenuAdapter(this));
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
        	return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectItem(position);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	private void selectItem(int position) {
		if (position == selected) {
	        mDrawerLayout.closeDrawer(mDrawerList);
			return;
		}
		
		selected = position;
		
		BaseFragment fragment = null;
		String titleName = getString(R.string.drilling_jobs);
		switch (position) {
		case 0:
			fragment = new JobsFragment();
			titleName = getString(R.string.jobs);
			break;
		case 1:
			fragment = new NotificationFragment();
			titleName = getString(R.string.notification);
			break;
		case 2:
			fragment = new FavouritesFragment();
			titleName = getString(R.string.favourites);
			break;
		case 3:
			fragment = new SettingsFragment();
			titleName = getString(R.string.settings);
			break;
		}
		
		if (fragment != null) {
			setTitle(titleName);
			
	        FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	        
	        mDrawerList.setItemChecked(position, true);
	        mDrawerLayout.closeDrawer(mDrawerList);
		}
    }

}
