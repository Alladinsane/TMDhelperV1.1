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
	//this activity is just our splash page
	MyDatabaseAdapter myDatabaseAdapter;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		myDatabaseAdapter = new MyDatabaseAdapter(this);
		myDatabaseAdapter.deleteTMDdatabase();
		loadData();
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
	public void loadData()
	{
		new Thread(new DataLoader()).start();
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
	class DataLoader implements Runnable
	{

		@Override
		public void run() {
			String[] brands = getResources().getStringArray(R.array.brands);
			for(int i=0; i<brands.length; i++)
			{
				String tempName = brands[i];
				int tempID = getResources().getIdentifier(tempName, "array",
						"com.example.tmdhelper");
				String[] product = getResources().getStringArray(tempID);

				//"Empty" should be the first option no matter what brand our user selects
				String empty = getResources().getString(R.string.default_entry);
				String emptyColor = "@color/white";
				int emptyCount = 0;
				int emptyShelf = 0;

				long emptyId = myDatabaseAdapter.insertDataProducts(empty, emptyColor, emptyCount, emptyShelf);

				if(emptyId<0)
				{
					Log.d("Mine", "Insert was unsuccessful");
				}
				else
				{
					Log.d("Mine", "Successfully added " + empty);
				}
				for(int j=0; j<product.length; j++)
				{
					String s = product[j];
					StringTokenizer st = new StringTokenizer(s);
					while(st.hasMoreTokens())
					{
						//unpack info from array store product data 
						String name = st.nextToken();
						String color = st.nextToken();
						int caseCount = Integer.parseInt(st.nextToken());
						int shelfCount = Integer.parseInt(st.nextToken());

						long id = myDatabaseAdapter.insertDataProducts(name, color, caseCount, shelfCount);

						if(id<0)
						{
							Log.d("Mine", "Insert was unsuccessful");
						}
						else
						{
							Log.d("Mine", "Successfully added " + name);
						}
					}
					continue;
				}
				continue;
			}

		}

	}
}
