package com.example.tmdhelper;



import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class BrandSelector extends MainActivity implements OnClickListener{
	//this activity will ask the user to select which product lines will be used
	//in this build. This info will be used to determine which product arrays
	//do not need to be loaded to complete this display modeling
	int[] button_resources={R.id.add_brands};
	ArrayList<String> brands = new ArrayList<String>();
	ArrayAdapter<String> adapt;
	MyDatabaseAdapter myDatabaseAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brand_selector_layout);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		ListView selectBrand = (ListView)findViewById(R.id.add_list_view);

		adapt = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_list_item_multiple_choice,
				(getResources().getStringArray(R.array.brands)));//Brand names for 
		//the ListView is populated
		//from an array stored in 
		//in resources
		selectBrand.setItemsCanFocus(false);
		selectBrand.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);//allows user to make multiple selections
		registerForContextMenu(selectBrand);
		selectBrand.setAdapter(adapt);
		for (int i=0; i < button_resources.length; i++)
		{
			Button b = (Button)findViewById(button_resources[i]);
			b.setOnClickListener(this);
		}
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
		if (id == R.id.action_restart)
		{
			brands.clear();
			startActivity(new Intent(BrandSelector.this, TMDmenu.class));
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		int index = v.getId();
		ListView selectBrand = (ListView)findViewById(R.id.add_list_view);

		switch(index)
		{
		case R.id.add_brands:
			
			brands.clear();
			int cntChoice = selectBrand.getCount();
			String checked;
			SparseBooleanArray sparseBooleanArray = selectBrand.getCheckedItemPositions();
			for(int i = 0; i < cntChoice; i++)
			{
				if(sparseBooleanArray.get(i) == true) 
				{
					checked = selectBrand.getItemAtPosition(i).toString();
					brands.add(checked);
				}
			}
			if(brands.size()==0)
			{
				Context context = getApplicationContext();
        		CharSequence text = "Build must contain at least one product line.";
        		int duration = Toast.LENGTH_SHORT;

        		Toast toast = Toast.makeText(context, text, duration);
        		toast.show();
        		break;
			}
			else
			{
			//with user data stored, we proceed to next Activity
			Intent intent = new Intent(BrandSelector.this, DisplaySize.class);
			intent.putExtra("brands", brands);
			startActivity(intent);
			}
		}
	}
	public static int getStringIdentifier(Context context, String name) {
		return context.getResources().getIdentifier(name, "string", context.getPackageName());
	}
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}


}