package com.example.tmdhelper;

import java.util.ArrayList;
import java.util.StringTokenizer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
/*This class contains a variety of functions common to both FullTMD and LoproTMD activities
 * 
 */
abstract class TMDactivity extends MainActivity implements OnClickListener{
	
	Context context;
	int buttonID;
	static final int MAKE_PRODUCT_SELECTION=1, MULTIPLE_TMDS_PICKER=2;
	String[] planogram;
	public int[] resources;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
				int multiple = data.getIntExtra("result", 1);
				CheckBox cb = (CheckBox) findViewById(R.id.checkbox);
				if(cb.isChecked()){
					cb.toggle();}
				executeMultipleTMDs(multiple);
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
	public boolean planogramExists(String thisTMD)
	{
		if(myDatabaseAdapter.hasObject(thisTMD))
		{
			return true;
		}
		return false;
	}
	protected void storePlanogram(String name, String[] planogram)
	{
		long id = myDatabaseAdapter.insertDataTMDs(name, planogram[0], planogram[1], planogram[2], 
				planogram[3], planogram[4]);

		if(id<0)
		{
			Log.d("Mine", "Insert was unsuccessful");
		}
		else
		{
			Log.d("Mine", "Successfully added " + name);
		}
	}

	public void restorePlanogram(String thisTMD)
	{
		//Method to retrieve prior planogram and set it current
		String rawData = myDatabaseAdapter.getPlanogramData(thisTMD);
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
		initializePlanogram();
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
		Intent intent = new Intent(context, Results.class);
		intent.putExtra("brands", brands);
		startActivity(intent);
	}
	public void launchNumberPickerActivity()
	{
		int maxValue = getMaxValue();
		Intent intent = new Intent(context, NumberPickerActivity.class);
		intent.putExtra("maxValue", maxValue);
		startActivityForResult(intent, MULTIPLE_TMDS_PICKER);
	}
	public void launchItemSelectorActivity()
	{
		Intent intent = (new Intent(context, ItemSelector.class));
		intent.putExtra("brands", brands);
		startActivityForResult(intent, MAKE_PRODUCT_SELECTION);
	}
	public Button getButtonByID(int buttonID)
	{
		Button b = (Button) findViewById(buttonID);
		return b;
	}
	public void setPlanogram(String[] planogram)
	{
		this.planogram=planogram;
	}
	public void setBrandsArray(ArrayList<String> brands)
	{
		this.brands = brands;
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
	public void setContext(Context context)
	{
		this.context = context;
	}
	abstract int[] getButtonResources();
	abstract void initializePlanogram();
	abstract void setHeading();
	abstract int getShelfNumber(Button b);
	abstract void nextButtonAction();
	abstract Context getContext();
	abstract void storePlanogram();
	abstract void executeMultipleTMDs(int multiple);
	abstract int getMaxValue();
}