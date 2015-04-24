package com.example.tmdhelper;



import java.util.StringTokenizer;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tmdhelper.MyDatabaseAdapter.MySQLiteOpenHelper;

public class TMDmenu extends MainActivity {
	
	MyDatabaseAdapter myDatabaseAdapter;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		myDatabaseAdapter = new MyDatabaseAdapter(this);
		myDatabaseAdapter.deleteTMDdatabase();
		
		ListView menuList = (ListView) findViewById(R.id.menu_list);

		String[] items = { getResources().getString(R.string.menu_item_start)};
				//, getResources().getString(R.string.menu_item_edit)};
		//all menu options are retrieved from string resources and stored into 
		//string array, which is then packed into our listview

		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, 
				R.layout.menu_item, items);
		menuList.setAdapter(adapt);
		
		DataLoader();

		menuList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				TextView textview = (TextView) itemClicked;
				String strText = textview.getText().toString();
				if (strText.equalsIgnoreCase(getResources().getString(
						R.string.menu_item_start))) {
					//this menu button launches the first activity in the Display 
					//modeling process
					startActivity(new Intent(TMDmenu.this, BrandSelector.class));
				}
				else if (strText.equalsIgnoreCase(getResources().getString(
						R.string.menu_item_edit))) {
					//this button accesses the activities for user editing of
					//array of products as well as individual product arrays
					startActivity(new Intent(TMDmenu.this, ProductEditor.class));
				}
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
	public void DataLoader()
	{
		String[] brands = getResources().getStringArray(R.array.brands);
		for(int i=0; i<brands.length; i++)
		{
			String tempName = brands[i];
			int tempID = getResources().getIdentifier(tempName, "array",
					this.getPackageName());
			String[] product = getResources().getStringArray(tempID);
			
			//"Empty" should be the first option no matter what brand our user selects
			String empty = "empty";
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
