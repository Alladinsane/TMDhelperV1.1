package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;

public class MainActivity extends ActionBarActivity {
	MyDatabaseAdapter myDatabaseAdapter;
	public static final String TMD_PREFERENCES = "tmdPrefs";
	SharedPreferences tmdPrefs;
	ArrayList<String> brands = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Mine", "MainActivity OnCreate called.");
		super.onCreate(savedInstanceState);
		loadDatabaseAdapter();
	}
	public void hideActionBar()
	{
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
	}
	public void loadSharedPreferences()
	{
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);
	}
	public MyDatabaseAdapter getDatabaseAdapter()
	{
		return myDatabaseAdapter;
	}
	public void loadDatabaseAdapter()
	{
		myDatabaseAdapter = new MyDatabaseAdapter(this);
	}
	public SharedPreferences getMySharedPreferences()
	{
		loadSharedPreferences();
		return tmdPrefs;
	}
	public void wipeDatabase()
	{
		myDatabaseAdapter.deleteTMDdatabase();
	}
	public void resetCounter()
	{
		SharedPreferences.Editor prefEditor = tmdPrefs.edit();
		prefEditor.putInt("counter", 1);
		prefEditor.commit();
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
	public void alertDialogForRestart()
	{
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage(getResources().getString(R.string.restart_alert_message));
		builder1.setCancelable(true);
		builder1.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				wipeDatabase();
				Intent intent = new Intent(MainActivity.this, TMDmenu.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}});
		builder1.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				return;
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
		
	}
	public void loadData(ArrayList<String> brands)
	{
		loadDatabaseAdapter();
		this.brands = brands;
		new Thread(new DataLoader()).start();
	}
		class DataLoader implements Runnable
		{
			
			@Override
			public void run() {
				for(int i=0; i<brands.size(); i++)
				{
					String[] product = getArrayByName(brands.get(i));
					//"Empty" should be the first option no matter what brand our user selects
					createDefaultEmptyEntry();

					for(int j=0; j<product.length; j++)
					{
						storeProductData(product[j]);
						continue;
					}
					continue;
				}
			}

		}
		public void createDefaultEmptyEntry()
		{
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
