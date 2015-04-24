package com.example.tmdhelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends MainActivity {
//this activity is just our splash page
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		ImageView logo = (ImageView) findViewById(R.id.logo);
		TextView title = (TextView) findViewById(R.id.title);
		TextView version = (TextView) findViewById(R.id.version);
		Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
		logo.startAnimation(fade1);//fades in our logo
		title.startAnimation(fade2);//title and version fade in together
		version.startAnimation(fade2);

	

		fade2.setAnimationListener(new AnimationListener(){
			//Once animations have completed, our splash page launches
			//the main menu activity
			public void onAnimationEnd(Animation animation){
				startActivity(new Intent(Splash.this,
						TMDmenu.class));
				Splash.this.finish();
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
			if (id == R.id.action_settings) {
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
	}
	