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
	Animation fade1, fade2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		ImageView logo = (ImageView) findViewById(R.id.logo);
		TextView title = (TextView) findViewById(R.id.title);
		TextView version = (TextView) findViewById(R.id.version);
		
		getDatabaseAdapter();
		myDatabaseAdapter.deleteTMDdatabase();
		loadData();
		loadAnimations();
		
		try{
		logo.startAnimation(fade1);//fades in our logo
		title.startAnimation(fade2);//title and version fade in together
		version.startAnimation(fade2);
		}
		catch(NullPointerException e)
		{
			System.out.println("No animations were loaded");
		}



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
	public void loadData()
	{
		new Thread(new DataLoader()).start();
	}
	public void getDatabaseAdapter()
	{
		myDatabaseAdapter = new MyDatabaseAdapter(this);
		
	}
	public void loadAnimations()
	{
		fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
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
				String[] product = getArrayByName(brands[i]);
				//"Empty" should be the first option no matter what brand our user selects
				createDefaultEntry();

				for(int j=0; j<product.length; j++)
				{
					storeProductData(product[j]);
					continue;
				}
				continue;
			}
		}

	}
	public void createDefaultEntry()
	{
		//this method creates an entry in the database for the default white, empty condition
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
	}
	public void storeProductData(String s)
	{
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

	}
	public String[] getArrayByName(String name)
	{
		int tempID = getResources().getIdentifier(name, "array",
				"com.example.tmdhelper");
		String[] product = getResources().getStringArray(tempID);
		return product;
	}
}