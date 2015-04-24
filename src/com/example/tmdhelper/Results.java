package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Results extends MainActivity implements OnClickListener {

	MyDatabaseAdapter myDatabaseAdapter;
	ArrayList<String> products;
	ArrayList<String> results= new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		int[] resources = {R.id.start_over, R.id.exit};
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setOnClickListener(this);
		}
		
		myDatabaseAdapter=new MyDatabaseAdapter(this);
		
		products = loadItemArray();
		
		processRawData();
	}
	public void processRawData()
	{
		ArrayList<String> allItems = myDatabaseAdapter.getAllItems();
		ArrayList<String> itemTallies = new ArrayList<String>();
		
		for(int i=0; i<products.size(); i++)
		{
			int count=0;
			for(int j=0; j<allItems.size(); j++)
			{
				if(products.get(i).equals(allItems.get(j)))
				{
					count++;
				}
			}
			if(count == 0)
			{
				continue;
			}
			String itemName = products.get(i);
			String item = itemName + " " + count;
			itemTallies.add(item);
		}
		for(int i=0; i<itemTallies.size(); i++)
		{
			String item = itemTallies.get(i);
			StringTokenizer st = new StringTokenizer(item);
			int tally = 0;
			String itemName = " ";
			while(st.hasMoreTokens())
			{
				itemName = st.nextToken();
				tally = Integer.parseInt(st.nextToken());
			}
			int total = calculateCaseCount(itemName, tally);
			addToList(itemName, total);
		}
		itemTallies.clear();
		allItems.clear();
		displayResults();
	}
	public int calculateCaseCount(String itemName, int shelves)
	{
		int totalCases=0;
		String name;
		String color;
		int caseCount=0;
		int shelfCount=0;	
		String productData = myDatabaseAdapter.getProductData(itemName);
		StringTokenizer st = new StringTokenizer(productData);
		while(st.hasMoreTokens())
		{
			//unpack info from array store product data to UserSelections
			name = st.nextToken();
			color = st.nextToken();
			caseCount = Integer.parseInt(st.nextToken());
			shelfCount = Integer.parseInt(st.nextToken());	
		}
		int eaches = shelfCount * shelves;
		int mod = 0;
		if(eaches%caseCount>0)
			mod = 1;
		totalCases = eaches/caseCount + mod;
		mod = 0;
		return totalCases;
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
		brands.clear();
		return itemArray;
	}
	public void addToList(String itemName, int total)
	{
		String phrase = itemName + ": " + total + " cases.";
		results.add(phrase);
	}
	public void displayResults()
	{
		ListView selectItem = (ListView)findViewById(R.id.results_view);


		ArrayAdapter adapt = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_list_item_1,
				results );

		selectItem.setAdapter(adapt); 
		selectItem.setItemsCanFocus(false);
		selectItem.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//allows user to make multiple selections
		registerForContextMenu(selectItem);
		selectItem.setAdapter(adapt);

	}
	@Override
	public void onClick(View v) {
		int index = v.getId();
		
		if(index== R.id.start_over)
		{
			myDatabaseAdapter.deleteTMDdatabase();
			results.clear();
			products.clear();
			startActivity(new Intent(Results.this, TMDmenu.class));
			finish();
		}
		/*else if(index== R.id.exit)
		{
			myDatabaseAdapter.deleteTMDdatabase();
			results.clear();
			products.clear();
			
	        Intent intent = new Intent(Intent.ACTION_MAIN); 
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	        intent.addCategory(Intent.CATEGORY_HOME); 
	        startActivity(intent);	
	        finish();
		}
		*/
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
