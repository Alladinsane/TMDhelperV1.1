package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
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
/*This class contains a variety of functions common to both FullTMD and LoproTMD activities
 * 
 */
abstract class TMDactivity extends MainActivity implements OnClickListener{
	int tmdTotal=0, subcounter=1, subTotal=1, multiple, loproTMD=0, fullTMD=0;
	public String TMDname, shelf5;
	private int COUNTER=1;
	ArrayList<String> brands = new ArrayList<String>();
	public static final String TMD_PREFERENCES = "tmdPrefs";
	SharedPreferences tmdPrefs;
	int buttonID;
	static final int MAKE_PRODUCT_SELECTION=1, MULTIPLE_TMDS_PICKER=2;
	String[] planogram;
	public int[] resources;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSharedPreferences();
		getDatabaseAdapter();
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
			}
		}	
	}
	protected void processResult(String item)
	{
		String productData = myDatabaseAdapter.getProductData(item);
		StringTokenizer st = new StringTokenizer(productData);
		while(st.hasMoreTokens())
		{
			Button b = getButtonByID(buttonID);
			String name = extractDataAndSetButton(st, b);
			int shelf=getShelfNumber(b);

			if(shelf>=0)
				saveToPlanogram(shelf, name);
			else
				Log.d("Mine", "no shelf number was recorded");
		}
	}
	public String extractDataAndSetButton(StringTokenizer pd, Button b)
	{
		String brandName = pd.nextToken();
		int thisColorID = getColorID(pd.nextToken());
		Integer.parseInt(pd.nextToken());
		Integer.parseInt(pd.nextToken());

		//change shelf Button text to item name, and background color to item color
		b.setText(brandName);
		b.setBackgroundColor(thisColorID);

		return brandName;
	}
	public boolean planogramExists()
	{
		if(myDatabaseAdapter.hasObject(TMDname+subcounter))
		{
			return true;
		}
		return false;
	}
	protected void storePlanogram()
	{
		String name = TMDname + subcounter;

		long id = myDatabaseAdapter.insertDataTMDs(name, planogram[0], planogram[1], planogram[2], planogram[3], shelf5);

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
		String name = TMDname + tmdNumber;
		String rawData = myDatabaseAdapter.getPlanogramData(name);
		StringTokenizer st = new StringTokenizer(rawData);

		for(int i=0; i<planogram.length; i++)
		{
			Log.d("Mine", "Adding planogram[" + i + "]");
			planogram[i] = st.nextToken();
			StringTokenizer names = new StringTokenizer(planogram[i]);

			while(names.hasMoreTokens())
			{
				String item = names.nextToken();
				String productData = myDatabaseAdapter.getProductData(item);
				StringTokenizer pd = new StringTokenizer(productData);
				while(pd.hasMoreTokens())
				{
					Button b = getButtonByID(resources[i]);
					extractDataAndSetButton(pd, b);
				}	
			}
		}
	}
	@Override
	public void onClick(View v) {
		int index = v.getId();

		if(index==R.id.next_button)
		{
			if(anyShelvesAreLeftEmpty())
			{
				launchEmptyAlertDialog();
			}
			else
				if(multipleTMDBoxIsChecked())
				{
					launchNumberPickerActivity();
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
			launchItemSelectorActivity();
		}
	}
	protected void nextButtonAction()
	{
		subcounter++;
		COUNTER++;
		Button b = getButtonByID(R.id.next_button);
		if(subcounter<tmdTotal)
		{
			b.setText("Next-->");
			String thisTMD= TMDname + subcounter;
			if(myDatabaseAdapter.hasObject(thisTMD))
			{
				restorePlanogram(subcounter);
			}
			else
				resetScreen();
		}
		else if(COUNTER==tmdTotal)
		{
			b.setText("Finish");
			if(myDatabaseAdapter.hasObject(TMDname+subcounter))
			{
				restorePlanogram(subcounter);
			}
			else
				resetScreen();
		}
		else
		{
			subcounter--;
			COUNTER--;
			launchResultsActivity();
		}
	}
	public void onBackPressed() {
		Button b = (Button) findViewById(R.id.next_button);
		b.setText("Next-->");
		storePlanogram();
		if(subcounter>1)
		{
			subcounter--;
			restorePlanogram(subcounter);
		}
		else
		{
			super.onBackPressed();
			finish();
		}
	}
	public boolean anyShelvesAreLeftEmpty()
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
	protected void saveToPlanogram(int shelf, String brand)
	{
		planogram[shelf] = brand;
	}
	public boolean multipleTMDBoxIsChecked()
	{
		CheckBox cb = (CheckBox) findViewById(R.id.checkbox);
		if(cb.isChecked())
		{
			return true;
		}
		else
			return false;
	}
	public void resetScreen()
	{
		setHeading(subcounter);
		for (int i=0; i <(resources.length-2); i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setText("empty");
			b.setBackgroundColor(getResources().getColor(R.color.white));
		}

	}
	public void launchEmptyAlertDialog()
	{
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage("Some shelves will be left empty.");
		builder1.setCancelable(true);
		builder1.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if(multipleTMDBoxIsChecked())
				{
					launchNumberPickerActivity();
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
	public void launchResultsActivity()
	{
		Intent intent = new Intent(TMDactivity.this, Results.class);
		intent.putExtra("brands", brands);
		startActivity(intent);
	}
	public void launchNumberPickerActivity()
	{
		Intent intent = new Intent(TMDactivity.this, NumberPickerActivity.class);
		intent.putExtra("maxValue", (subTotal-subcounter)+1);
		startActivityForResult(intent, MULTIPLE_TMDS_PICKER);
	}
	public void launchItemSelectorActivity()
	{
		Intent intent = (new Intent(TMDactivity.this, ItemSelector.class));
		intent.putExtra("brands", brands);
		startActivityForResult(intent, MAKE_PRODUCT_SELECTION);
	}
	public Button getButtonByID(int buttonID)
	{
		Button b = (Button) findViewById(buttonID);
		return b;
	}
	public void setShelf5(String shelf5)
	{
		this.shelf5=shelf5;
	}
	public void setPlanogram(String[] planogram)
	{
		this.planogram=planogram;
	}
	public void setBrandsArray(ArrayList<String> brands)
	{
		this.brands = brands;
	}
	public void setTMDname(String TMDname)
	{
		this.TMDname=TMDname;
	}
	public void setSubCounter(int counter)
	{
		this.subcounter=counter;
	}
	public int getCounter()
	{
		return subcounter;
	}
	public void setFullTMD(int fullTMD)
	{
		this.fullTMD=fullTMD;
	}
	public void setLoproTMD(int loproTMD)
	{
		this.loproTMD=loproTMD;
	}
	public void setTMDtotal(int tmdTotal)
	{
		this.tmdTotal=tmdTotal;
	}
	public void setSubTotal(int subTotal)
	{
		this.subTotal=subTotal;
	}
	public void setButtonResources(int[] resources)
	{
		this.resources = resources;
	}
	
	public int getColorID(String color)
	{
		int tempID = getResources().getIdentifier(color, color, getPackageName());
		int thisColor= getResources().getColor(tempID);
		return thisColor;
	}
	abstract int[] getButtonResources();
	abstract void initializePlanogram();
	abstract void setHeading(int number);
	abstract int getShelfNumber(Button b);

	private class MultipleTMD extends AsyncTask <Void, Void, String>
	{
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(
					TMDactivity.this,
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
				subcounter++;
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