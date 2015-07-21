package com.example.tmdhelper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.content.SharedPreferences;

abstract class BaseActivity extends ActionBarActivity {
	MyDatabaseAdapter myDatabaseAdapter;
	public static final String TMD_PREFERENCES = "tmdPrefs";
	SharedPreferences tmdPrefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getMySharedPreferences();
		getDatabaseAdapter();
	}
	public void hideActionBar()
	{
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
	}
	public void getMySharedPreferences()
	{
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);
	}
	public void getDatabaseAdapter()
	{
		myDatabaseAdapter = new MyDatabaseAdapter(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_reset) {
			return true;
		}
		else if(id == R.id.action_restart)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
