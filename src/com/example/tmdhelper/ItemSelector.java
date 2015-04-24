package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ItemSelector extends MainActivity {
	
	ArrayAdapter<String> adapt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_selector);
		
		ArrayList<String> itemArray = loadItemArray();
		Log.d("Mine", "itemArray = " + itemArray);
		ListView selectItem = (ListView)findViewById(R.id.item_list_view);
		

		adapt = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                itemArray );

        selectItem.setAdapter(adapt); 
		selectItem.setItemsCanFocus(false);
		selectItem.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//allows user to make multiple selections
		registerForContextMenu(selectItem);
		selectItem.setAdapter(adapt);
		
		selectItem.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				TextView textview = (TextView) itemClicked;
				String strText = textview.getText().toString();
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", strText);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
			});
	}
	public ArrayList<String> loadItemArray()
	{
		ArrayList<String> brands = getIntent().getStringArrayListExtra("brands");
		Log.d("Mine", "ItemSelector brands = " + brands);
		ArrayList<String> itemArray = new ArrayList<String>();
		
		for(int i=0; i<brands.size(); i++)
		{
			String myBrand = brands.get(i).toString();
			Log.d("Mine", "myBrand = " +myBrand);
			int id = this.getResources().getIdentifier(myBrand, "array", this.getPackageName());
			String[] tempArray = getResources().getStringArray(id);
			Log.d("Mine", "tempArray = " + tempArray);
			for(int j=0; j<tempArray.length; j++)
			{
				String temp = tempArray[j].toString();
				Log.d("Mine", "temp = " + temp);
				StringTokenizer st = new StringTokenizer(temp);
				
				String item = st.nextToken();
				itemArray.add(item);
			}
		}
		itemArray.add("empty");
		return itemArray;
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
		if (id == R.id.action_settings) {
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
