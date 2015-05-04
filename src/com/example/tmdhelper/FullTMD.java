package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class FullTMD extends MainActivity implements OnClickListener{
	static final int MAKE_PRODUCT_SELECTION=1;
	static final int MULTIPLE_TMDS_PICKER=2;
	public static final String TMD_PREFERENCES = "tmdPrefs";
	int tmdTotal;
	int loproTMD;
	int counter=1;
	int buttonID;
	int multiple;
	//String defaultEntry = getResources().getString(R.string.default_entry);
	SharedPreferences tmdPrefs;
	ArrayList<String> brands = new ArrayList<String>();
	String[] planogram = new String[]{"empty", "empty", "empty", "empty", "empty"};
	//our planogram is instantiated with all the shelves empty
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
			//we are only building one TMD, so our Next button is set to Finish
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Finish");
		}
		//each shelf on the TMD is represented in the layout as a button
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
				R.id.next_button, R.id.checkbox};
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setOnClickListener(this);
		}
		
		if(myDatabaseAdapter.hasObject("fullTMD"+counter))
		{
			restorePlanogram(counter);
		}
		setHeading(counter);

	}
	
	public void setHeading(int i)
	{
		//adds the tmd number to the header
		TextView header = (TextView) findViewById(R.id.TMDnumber);
		String heading = "Full TMD #" + i + " of " + tmdTotal;
		header.setText(heading);
	}
	public void resetScreen()
	{
		Log.d("Mine", "Reset has been called");
		setHeading(counter);
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5};
        for (int i=0; i <resources.length; i++)
        {
        	//resets each button to "empty"
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
		//checks to see if any shelves have been left empty
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
		getMenuInflater().inflate(R.menu.planogram, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_reset) {
			planogram=new String[]{"empty", "empty", "empty", "empty", "empty"};
			resetScreen();
			return true;
		}
		else if(id == R.id.action_restart)
		{
			myDatabaseAdapter.deleteTMDdatabase();
			startActivity(new Intent(FullTMD.this, TMDmenu.class));
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
				//if some shelves are empty, and alert gives the user the chance
				//to cancel "Next" and go back to the planogram, or leave them
				//empty and continue
				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				builder1.setMessage(getResources().getString(R.string.empty_alert_message));
				builder1.setCancelable(true);
				builder1.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						if(boxIsChecked())
						{
							Intent intent = new Intent(FullTMD.this, NumberPickerActivity.class);
							intent.putExtra("maxValue", (tmdTotal-counter)+1);
							startActivityForResult(intent, MULTIPLE_TMDS_PICKER);
						}
						else
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
				if(boxIsChecked())
				{
					Intent intent = new Intent(FullTMD.this, NumberPickerActivity.class);
					intent.putExtra("maxValue", (tmdTotal-counter)+1);
					startActivityForResult(intent, MULTIPLE_TMDS_PICKER);
				}
				else
					nextButtonAction();
		}
		else if(index==R.id.checkbox)
		{
			return;
		}
		else//all other onClicks are shelf buttons
		{
			//calls the activity that allows the user to select an item for this shelf
			buttonID=index;
			Intent intent = (new Intent(FullTMD.this, ItemSelector.class));
			intent.putExtra("brands", brands);
			startActivityForResult(intent, MAKE_PRODUCT_SELECTION);
		}
		
	}
	protected void nextButtonAction()
	{
		
		storePlanogram();
		//the current planogram is saved to the database
		counter++;//tmd counter is incremented
		if(counter<tmdTotal)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Next-->");
			if(myDatabaseAdapter.hasObject("fullTMD"+counter))
			{
				restorePlanogram(counter);
			}
			else
				resetScreen();
		}
		else if(counter==tmdTotal)
		{
			//if this our last full TMD and no low profile tmd's have been selected for this
			//build
			if(loproTMD==0)
			{
				//our "Next" button changes to a "finish" button
				
				Button b = (Button) findViewById(R.id.next_button);
				b.setText("Finish");
				if(myDatabaseAdapter.hasObject("fullTMD"+counter))
				{
					restorePlanogram(counter);
				}
				else
				resetScreen();
			}
			else
				//otherwise, "Next" button is left as is and planogram is reset
				if(myDatabaseAdapter.hasObject("fullTMD"+counter))
				{
					restorePlanogram(counter);
				}
				else
				resetScreen();
		}
		else
		{
			if(loproTMD>0)
			{
				//there are no more Full TMDs in this build, but Low Profile TMDs were selected
				counter--;
				Intent intent = new Intent(FullTMD.this, LoproTMD.class);
				intent.putExtra("brands", brands);
				startActivity(intent);
			}
			else
			{
				//this build is done, and the results activity is called
				counter--;
				Intent intent = new Intent(FullTMD.this, Results.class);
				intent.putExtra("brands", brands);
				startActivity(intent);
			}
		}
		}
	
	protected void processResult(String item)
	{
		//method for handling user selected item for shelf
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
			//change shelf Button text to item name, and background color to item color
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
				//user has made a selection, and it is temporarily stored to current
				//planogram ArrayList
				saveToPlanogram(shelf, name);
			}
			else
				//We should never get here
				Log.d("Mine", "no shelf number was recorded");
		}
	}
	protected void saveToPlanogram(int shelf, String brand)
	{
		//stores this shelf to the overall planogram for this tmd
		planogram[shelf] = brand;
	}
	protected void storePlanogram()
	{
		//saves this tmd's final planogram to the database
		String name = "fullTMD" + counter;
		Log.d("Mine", name);
		String shelf1=planogram[0];
		Log.d("Mine", shelf1);
		String shelf2=planogram[1];
		Log.d("Mine", shelf2);
		String shelf3=planogram[2];
		Log.d("Mine", shelf3);
		String shelf4=planogram[3];
		Log.d("Mine", shelf4);
		String shelf5=planogram[4];
		Log.d("Mine", shelf5);

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
	public void restorePlanogram(int tmdNumber)
	{
		//Method to retrieve prior planogram and set it current
		setHeading(tmdNumber);
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5};
		String name = "fullTMD" +tmdNumber;
		String rawData = myDatabaseAdapter.getPlanogramData(name);

		StringTokenizer st = new StringTokenizer(rawData);

		while(st.hasMoreTokens())
		{
			for(int i=0; i<=4; i++)
			{
				planogram[i] = st.nextToken();
			}
		}
		for(int i=0; i<planogram.length; i++)
		{
			StringTokenizer names = new StringTokenizer(planogram[i]);

			while(names.hasMoreTokens())
			{
				String item = names.nextToken();
				String productData = myDatabaseAdapter.getProductData(item);
				StringTokenizer pd = new StringTokenizer(productData);
				while(pd.hasMoreTokens())
				{
					//unpack info from array 
					String s = pd.nextToken();
					String color = pd.nextToken();
					Integer.parseInt(pd.nextToken());
					Integer.parseInt(pd.nextToken());

					int tempID = getResources().getIdentifier(color, color, getPackageName());
					int thisColor= getResources().getColor(tempID);
					//change shelf Button text to item name, and background color to item color
					Button b = (Button) findViewById(resources[i]);
					b.setText(s);
					b.setBackgroundColor(thisColor);
				}	
			}
		}
	}
	@Override
	public void onBackPressed() {
		if(counter>1)
		{
			storePlanogram();
			counter--;
			restorePlanogram(counter);
		}
		else
		{
			storePlanogram();
			super.onBackPressed();
		}
	}
	public boolean boxIsChecked()
	{
		CheckBox cb = (CheckBox) findViewById(R.id.checkbox);
		if(cb.isChecked())
		{
			return true;
		}
		else
			return false;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//should be returning with the user selected item for this shelf
		Log.d("Mine", "Returned to FullTMD");
		if (requestCode == MAKE_PRODUCT_SELECTION) {
			if(resultCode == RESULT_OK){
	            String result=data.getStringExtra("result");
	            processResult(result);
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
		else if (requestCode == MULTIPLE_TMDS_PICKER)
		{
			if(resultCode == RESULT_OK){
			multiple = data.getIntExtra("result", 1);
			CheckBox cb = (CheckBox) findViewById(R.id.checkbox);
			if(cb.isChecked()){
	            cb.toggle();}
			new MultipleTMD().execute();
			
			}
			if (resultCode == RESULT_CANCELED) {
				Log.d("Mine", "There was no result");
	            //Write your code if there's no result
	        }
			
		}
}

private class MultipleTMD extends AsyncTask <Void, Void, String>
{
    private ProgressDialog dialog;

    @Override
    protected void onPreExecute()
    {
        dialog = ProgressDialog.show(
            FullTMD.this,
            "Filling Multiple Towers",
            "Please wait...", 
            true);
    }

    @Override
    protected String doInBackground(Void... params)
    {
		for(int i=1; i<multiple; i++)
		{
			storePlanogram();
			counter++;
		}
        
		return "";
    }

    @Override
    protected void onPostExecute(String result)
    {
    	nextButtonAction();
        dialog.dismiss();
    }
}
}