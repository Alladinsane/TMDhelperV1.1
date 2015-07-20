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
public class LoproTMD extends TMDactivity implements OnClickListener{
	public String TMDname="loproTMD";
	final String TITLE="Low Profile";
	ArrayList<String> brands = new ArrayList<String>();
	int counter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTMDname(TMDname);
		setContentView(R.layout.lopro_tmd);
		resources = getButtonResources();
		super.setButtonResources(resources);
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);
		myDatabaseAdapter = new MyDatabaseAdapter(this);
		brands = getIntent().getStringArrayListExtra("brands");
		initializePlanogram();
		super.setBrandsArray(brands);
		super.resetScreen();
		
		if(tmdPrefs.contains("loproTMD"))
		{
			loproTMD = tmdPrefs.getInt("loproTMD", 0);
			super.setLoproTMD(loproTMD);
		}
		tmdTotal = loproTMD+fullTMD;
		super.setSubTotal(loproTMD);
		super.setTMDtotal(tmdTotal);
		if(tmdTotal == 1)
		{
			Button b = (Button) findViewById(R.id.next_button);
			b.setText("Finish");
		}
		resources = getButtonResources();
		for (int i=0; i <resources.length; i++)
		{
			Button b = (Button)findViewById(resources[i]);
			b.setOnClickListener(this);
		}
		counter=tmdPrefs.getInt("counter", 1);
		if(planogramExists())
		{
			restorePlanogram(counter);
		}
		setHeading(counter);
	}
	public int[] getButtonResources()
	{
		int[] resources = {R.id.button1, R.id.button2, R.id.button3, R.id.button4,
				R.id.next_button, R.id.checkbox};
		return resources;
	}
	public void setHeading(int number)
	{
		TextView header = (TextView) findViewById(R.id.TMDnumber);
		String heading = this.TITLE +" #" +  number + " of " + loproTMD;
		header.setText(heading);
	}
	
	public int getShelfNumber(Button b)
	{
		int shelfNumber=-1;
		switch(b.getId())
		{
		case R.id.button1:
			shelfNumber=0;
			break;
		case R.id.button2:
			shelfNumber=1;
			break;
		case R.id.button3:
			shelfNumber=2;
			break;
		case R.id.button4:
			shelfNumber=3;
			break;
		}
		return shelfNumber;
	}
	public void initializePlanogram()
	{
		planogram = new String[4];
		for(int i=0; i<planogram.length; i++)
		{
			planogram[i] = "empty";
		}
		super.setPlanogram(planogram);
	}
	protected void storePlanogram()
	{
		String shelf5="no_shelf";
		super.setShelf5(shelf5);
		super.storePlanogram();
	}
	protected void nextButtonAction()
	{
		int counter=super.getCounter();
		storePlanogram();
		resetScreen();

		if(counter==loproTMD)
		{
			storePlanogram();
			Intent intent = new Intent(LoproTMD.this, Results.class);
			intent.putExtra("brands", brands);
			startActivity(intent);
			finish();
		}
		else
			super.nextButtonAction();
	}
	public void onBackPressed() {
		counter--;
		if(counter>0)
			super.restorePlanogram(counter);
		else
		{
			int fullTMD = tmdPrefs.getInt("fullTMD", 0);
			super.setTMDname("fullTMD");
			SharedPreferences.Editor prefEditor = tmdPrefs.edit();
			prefEditor.putInt("counter", fullTMD);
			prefEditor.commit();
			startActivity(new Intent(LoproTMD.this, FullTMD.class));
			finish();
		}
	}
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
			initializePlanogram();
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

}