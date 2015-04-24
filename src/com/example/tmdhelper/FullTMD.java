package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FullTMD extends MainActivity implements OnClickListener{
	static final int MAKE_PRODUCT_SELECTION=1;
	public static final String TMD_PREFERENCES = "tmdPrefs";
	int tmdTotal;
	int loproTMD;
	int counter=1;
	int buttonID;
	SharedPreferences tmdPrefs;
	ArrayList<String> brands = new ArrayList<String>();
	String[] planogram = new String[]{"empty", "empty", "empty", "empty", "empty"};
	MyDatabaseAdapter myDatabaseAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_tmd);
		brands = getIntent().getStringArrayListExtra("brands");
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);
		myDatabaseAdapter = new MyDatabaseAdapter(this);
		
		if(tmdPrefs.contains("fullTMD"))
		{
			tmdTotal = tmdPrefs.getInt("fullTMD", 0);
		}

		if(tmdPrefs.contains("loproTMD"))
		{
			loproTMD = tmdPrefs.getInt("loproTMD", 0);
		}
		if(loproTMD==0 && tmdTotal == 1)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Finish");
		}

		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
				R.id.next_button, R.id.checkbox};
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setOnClickListener(this);
		}

		setHeading(counter);

	}
	
	public void setHeading(int i)
	{
		TextView header = (TextView) findViewById(R.id.TMDnumber);
		String heading = "Full TMD #" + i + " of " + tmdTotal;
		header.setText(heading);
	}
	public void resetScreen()
	{
		
		setHeading(counter);
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5};
        for (int i=0; i <resources.length; i++)
        {
        	Button b = (Button)findViewById(resources[i]);
        	b.setText("empty");
        	b.setBackgroundColor(getResources().getColor(R.color.white));
        }
        for(int i=0; i<planogram.length; i++)
        {
        	planogram[i]="empty";
        }
		
	}
	public boolean checkForEmpty()
	{
		for(int i=0; i<planogram.length; i++)
		{
			if(planogram[i].equals("empty"))
			{
				return true;
			}
		}
		return false;
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

	@Override
	public void onClick(View v) {
		int index = v.getId();

		if(index==R.id.next_button)
		{
			if(checkForEmpty())
			{
				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				builder1.setMessage("Some shelves will be left empty.");
				builder1.setCancelable(true);
				builder1.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						nextButtonAction();
					}
				});
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
			else
				nextButtonAction();
		}
		else
		{
			buttonID=index;
			Intent intent = (new Intent(FullTMD.this, ItemSelector.class));
			intent.putExtra("brands", brands);
			startActivityForResult(intent, MAKE_PRODUCT_SELECTION);
		}
		
	}
	protected void nextButtonAction()
	{
		storePlanogram();
		counter++;
		if(counter<tmdTotal)
			resetScreen();
		else if(counter==tmdTotal)
		{
			if(loproTMD==0)
			{
				Button b = (Button) findViewById(R.id.next_button);
				b.setText("Finish");
				resetScreen();
			}
			else
				resetScreen();
		}
		else
		{
			if(loproTMD>0)
			{
				Intent intent = new Intent(FullTMD.this, LoproTMD.class);
				intent.putExtra("brands", brands);
				startActivity(intent);
				finish();
				
			}
			else
			{
				Intent intent = new Intent(FullTMD.this, Results.class);
				intent.putExtra("brands", brands);
				startActivity(intent);
				finish();
				
			}
		}
	
	}
	protected void processResult(String item)
	{
		String productData = myDatabaseAdapter.getProductData(item);
		StringTokenizer st = new StringTokenizer(productData);
		while(st.hasMoreTokens())
		{
			//unpack info from array store product data to UserSelections
			String name = st.nextToken();
			String color = st.nextToken();
			int caseCount = Integer.parseInt(st.nextToken());
			int shelfCount = Integer.parseInt(st.nextToken());
			
			int tempID = getResources().getIdentifier(color, color, getPackageName());
			int thisColor= getResources().getColor(tempID);
			
			Button b = (Button) findViewById(buttonID);
			b.setText(name);
			b.setBackgroundColor(thisColor);
			
			int shelf=-1;
			
			switch(b.getId())
			{
			case R.id.button1:
				shelf=0;
				break;
			case R.id.button2:
				shelf=1;
				break;
			case R.id.button3:
				shelf=2;
				break;
			case R.id.button4:
				shelf=3;
				break;
			case R.id.button5:
				shelf=4;
				break;
			}
			if(shelf>=0)
			{
				saveToPlanogram(shelf, name);
			}
			else
				Log.d("Mine", "no shelf number was recorded");
		}
	}
	protected void saveToPlanogram(int shelf, String brand)
	{
		planogram[shelf] = brand;
	}
	protected void storePlanogram()
	{
		String name = "fullTMD" + counter;
		String shelf1=planogram[0];
		String shelf2=planogram[1];
		String shelf3=planogram[2];
		String shelf4=planogram[3];
		String shelf5=planogram[4];

		long id = myDatabaseAdapter.insertDataTMDs(name, shelf1, shelf2, shelf3, shelf4, shelf5);
		
		if(id<0)
		{
			Log.d("Mine", "Insert was unsuccessful");
		}
		else
		{
			Log.d("Mine", "Successfully added " + name);
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MAKE_PRODUCT_SELECTION) {
			if(resultCode == RESULT_OK){
	            String result=data.getStringExtra("result");
	            processResult(result);
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
}
}