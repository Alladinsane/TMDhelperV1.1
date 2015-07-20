package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Intent;
import android.content.SharedPreferences;
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
	public static final String TMD_PREFERENCES = "tmdPrefs";
	SharedPreferences tmdPrefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);

		int[] resources = {R.id.start_over, R.id.exit};
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setOnClickListener(this);
		}
		
		myDatabaseAdapter=new MyDatabaseAdapter(this);
		
		products = loadItemArray();//products ArrayList will now contain 
		//the names of only those products user selected for use in this build
		processRawData();
	}
	public void processRawData()
	{
		ArrayList<String> allItems = myDatabaseAdapter.getAllItems();
		ArrayList<String> itemTallies = new ArrayList<String>();
		
		//loop through the list of products, and check it against the user
		//planogram items to see if it was used in this build. For each
		//individual item name, count is used to add up how many shelves of that item
		//exist in this build
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
			addToList(itemName, total);//calls a method to add the final string to an array
			//that will be used to create the Results ListView
		}
		allItems.clear();//this array is no longer needed and is reset for next build
		displayResults();//method that creates the ListView
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
			//unpack product data from array 
			name = st.nextToken();
			color = st.nextToken();
			caseCount = Integer.parseInt(st.nextToken());
			shelfCount = Integer.parseInt(st.nextToken());	
		}
		int eaches = shelfCount * shelves;//the total number of shelves to be filled by this 
		//item multiplied by the number of bags of this item required to fill a shelf
		int mod = 0;
		if(eaches%caseCount>0)//if total needed bags is not evenly divisible by caseCount
			mod = 1;          //an extra case will be needed to completely fill
		totalCases = eaches/caseCount + mod;
		mod = 0;
		return totalCases;
	}
	public ArrayList<String> loadItemArray()
	{
		ArrayList<String> brands = getIntent().getStringArrayListExtra("brands");
		ArrayList<String> itemArray = new ArrayList<String>();
		//pulls out the name of each item, and adds it to an itemArray
		for(int i=0; i<brands.size(); i++)
		{
			String myBrand = brands.get(i).toString();
			Log.d("Mine", "myBrand = " +myBrand);
			//using the product line's name to get the the matching product list from resources
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
		//the name and total count of each item in this build is now used
		//to create a String which communicates the results to the user
		String phrase = itemName + ": " + total + " cases.";
		results.add(phrase);
	}
	public void displayResults()
	{
		//this method creates the ListView that contains actual results
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
			//make sure all planograms and arrays are cleared and return to main menu
			myDatabaseAdapter.deleteTMDdatabase();
			results.clear();
			products.clear();
			Intent intent = new Intent(Results.this, TMDmenu.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}
		/*decided not to implement exit button at this time
		 * 
		 * else if(index== R.id.exit)
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
	public void onBackPressed()
	{
		
		int loproTMD=0;
		if(tmdPrefs.contains("loproTMD"))
		{
			loproTMD = tmdPrefs.getInt("loproTMD", 0);
		}
		if(loproTMD>0)
		{
			SharedPreferences.Editor prefEditor = tmdPrefs.edit();
			prefEditor.putInt("counter", loproTMD);
			prefEditor.commit();
			startActivity(new Intent(Results.this, LoproTMD.class));
		}
		else
		{
			int fullTMD = tmdPrefs.getInt("fullTMD", 0);
			SharedPreferences.Editor prefEditor = tmdPrefs.edit();
			prefEditor.putInt("counter", fullTMD);
			prefEditor.commit();
			startActivity(new Intent(Results.this, FullTMD.class));
		}
		finish();
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
			myDatabaseAdapter.deleteTMDdatabase();
			startActivity(new Intent(Results.this, TMDmenu.class));
			finish();
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
