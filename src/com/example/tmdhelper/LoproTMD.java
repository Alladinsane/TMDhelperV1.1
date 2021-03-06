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
/*This class functions in the same way as FullTMD, except that LoproTMDs have only 4 shelves
 * 
 */
public class LoproTMD extends MainActivity implements OnClickListener{
	int tmdTotal;
	int counter=1;
	int multiple;
	public static final String TMD_PREFERENCES = "tmdPrefs";
	SharedPreferences tmdPrefs;
	MyDatabaseAdapter myDatabaseAdapter;
	int buttonID;
	static final int MAKE_PRODUCT_SELECTION=1;
	static final int MULTIPLE_TMDS_PICKER=2;
	ArrayList<String> brands = new ArrayList<String>();
	String[] planogram = new String[]{"empty", "empty", "empty", "empty"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lopro_tmd);
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);
		myDatabaseAdapter = new MyDatabaseAdapter(this);brands = getIntent().getStringArrayListExtra("brands");
		brands = getIntent().getStringArrayListExtra("brands");

		if(tmdPrefs.contains("loproTMD"))
		{
			tmdTotal = tmdPrefs.getInt("loproTMD", 0);
		}
		if(tmdTotal == 1)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Finish");
		}
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4,
				R.id.next_button, R.id.checkbox};
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setOnClickListener(this);
		}
		if(myDatabaseAdapter.hasObject("loproTMD"+counter))
		{
			restorePlanogram(counter);
		}
		setHeading(counter);
	}

	public void setHeading(int i)
	{
		TextView header = (TextView) findViewById(R.id.TMDnumber);
		String heading = "Low Profile #" + i + " of " + tmdTotal;
		header.setText(heading);
	}
	public void resetScreen()
	{
		setHeading(counter);
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4};
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setText("empty");
			b.setBackgroundColor(getResources().getColor(R.color.white));
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
			}
			if(shelf>=0)
				saveToPlanogram(shelf, name);
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
		String name = "loproTMD" + counter;
		String shelf1=planogram[0];
		String shelf2=planogram[1];
		String shelf3=planogram[2];
		String shelf4=planogram[3];
		String shelf5="no_shelf";

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
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4};
		String name = "loproTMD" +tmdNumber;
		String rawData = myDatabaseAdapter.getPlanogramData(name);

		StringTokenizer st = new StringTokenizer(rawData);

		//while(st.hasMoreTokens())
		//{
			for(int i=0; i<=3; i++)
			{
				Log.d("Mine", "Adding planogram[" + i + "]");
				planogram[i] = st.nextToken();
			}
		//}
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
	public void onBackPressed() {
		if(counter>1)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Next-->");
			storePlanogram();
			counter--;
			restorePlanogram(counter);
		}
		else
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Next-->");
			storePlanogram();
			super.onBackPressed();
		}
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
			planogram=new String[]{"empty", "empty", "empty", "empty"};
			resetScreen();
			return true;
		}
		else if(id == R.id.action_restart)
		{
			myDatabaseAdapter.deleteTMDdatabase();
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage(getResources().getString(R.string.restart_alert_message));
			builder1.setCancelable(true);
			builder1.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					Intent intent = new Intent(LoproTMD.this, TMDmenu.class);
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
						if(boxIsChecked())
						{
							Log.d("Mine", "CheckBox is checked");
							Intent intent = new Intent(LoproTMD.this, NumberPickerActivity.class);
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
					Log.d("Mine", "CheckBox is checked");
					Intent intent = new Intent(LoproTMD.this, NumberPickerActivity.class);
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
		else
		{
			buttonID=index;
			Intent intent = (new Intent(LoproTMD.this, ItemSelector.class));
			intent.putExtra("brands", brands);
			startActivityForResult(intent, MAKE_PRODUCT_SELECTION);
		}
	}
	protected void nextButtonAction()
	{
		storePlanogram();
		counter++;
		if(counter<tmdTotal)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Next-->");
			if(myDatabaseAdapter.hasObject("loproTMD"+counter))
			{
				restorePlanogram(counter);
			}
			else
				resetScreen();
		}
		else if(counter==tmdTotal)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Finish");
			if(myDatabaseAdapter.hasObject("loproTMD"+counter))
			{
				restorePlanogram(counter);
			}
			else
				resetScreen();
		}
		else
		{
			counter--;
			Intent intent = new Intent(LoproTMD.this, Results.class);
			intent.putExtra("brands", brands);
			startActivity(intent);
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
			Log.d("Mine", "RequestCode properly identified");
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
            LoproTMD.this,
            "Filling Multiple Towers",
            "Please wait...", 
            true);
    }

    @Override
    protected String doInBackground(Void... params)
    {
    	Log.d("Mine", "applyMultiplePlanograms(" + multiple + ")");
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