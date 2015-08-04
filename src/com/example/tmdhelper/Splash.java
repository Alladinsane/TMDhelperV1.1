package com.example.tmdhelper;

import java.util.StringTokenizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends MainActivity {
	//this activity is just our splash page with some background setup
	Animation fade1, fade2;
	ImageView logo;
	TextView title, version;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		hideActionBar();
		
		findViews();
		
		loadSharedPreferences();
		wipeDatabase();
		
		loadAnimations();
		
		runAnimations();

		startNextActivityWhenAnimationCompletes();
	}
	public void loadAnimations()
	{
		fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
	}
	public void runAnimations()
	{
		try{
			logo.startAnimation(fade1);//fades in our logo
			title.startAnimation(fade2);//title and version fade in together
			version.startAnimation(fade2);
			}
			catch(NullPointerException e)
			{
				System.out.println("No animations were loaded");
			}
	}
	public void startNextActivityWhenAnimationCompletes()
	{
		fade2.setAnimationListener(new AnimationListener(){
			//Once animations have completed, our splash page launches
			//the main menu activity
			public void onAnimationEnd(Animation animation){
				Intent intent = buildIntent();
				startActivity(intent);
				Splash.this.finish();
			}
			public Intent buildIntent()
			{
				Intent intent = new Intent(Splash.this, TMDmenu.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				return intent;
			}
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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

	public void onPause() {
		super.onPause();
		//stop the animation
		ImageView logo = (ImageView) findViewById(R.id.logo);
		TextView title = (TextView) findViewById(R.id.title);
		TextView version = (TextView) findViewById(R.id.version);

		logo.clearAnimation();
		title.clearAnimation();
		version.clearAnimation();
	}
	public void findViews()
	{
		logo = (ImageView) findViewById(R.id.logo);
		title = (TextView) findViewById(R.id.title);
		version = (TextView) findViewById(R.id.version);
	}
}