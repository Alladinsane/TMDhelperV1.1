package com.example.tmdhelper;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class DisplaySize extends MainActivity implements OnClickListener{
//This Activity will gather user input to determine how many TMDs will be built
	public static final String TMD_PREFERENCES = "tmdPrefs";
	SharedPreferences tmdPrefs;
	private EditText fullInput;
	private EditText loproInput;
	MyDatabaseAdapter myDatabaseAdapter;
	ArrayList<String> brands;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_size_layout);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		tmdPrefs = getSharedPreferences(TMD_PREFERENCES, MODE_PRIVATE);
		myDatabaseAdapter = new MyDatabaseAdapter(this);
		brands = getIntent().getStringArrayListExtra("brands");
		
		
		fullInput = (EditText) findViewById(R.id.fullTMD_view);
		loproInput = (EditText) findViewById(R.id.lopro_view);
		final Button next = (Button) findViewById(R.id.next_button);
		
		myDatabaseAdapter.deleteTMDdatabase();
		
		next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	int fullTMD;
            	int loproTMD;
            	
            	
            	if(fullInput.getText().toString().matches(""))//error checking: if user
            		fullTMD=0;                                //entered nothing
            	else
            		fullTMD = Integer.parseInt(fullInput.getText().toString());

            	if(loproInput.getText().toString().matches(""))
            		loproTMD=0;
            	else
            		loproTMD = Integer.parseInt(loproInput.getText().toString());

            	if(fullTMD + loproTMD <= 0){//if no values have been entered
            		Context context = getApplicationContext();
            		CharSequence text = "Build must contain at least one TMD.";
            		int duration = Toast.LENGTH_SHORT;

            		Toast toast = Toast.makeText(context, text, duration);
            		toast.show();
            	}
            	else
            	{
            		SharedPreferences.Editor prefEditor = tmdPrefs.edit();
            		prefEditor.putInt("fullTMD", fullTMD);
            		prefEditor.putInt("loproTMD", loproTMD);
            		prefEditor.putInt("counter", 1);
            		prefEditor.commit();
            		
            		//values are stored and
            		//TMD activity is launched
            		ArrayList<String> brands = getIntent().getStringArrayListExtra("brands");
            		if(fullTMD>0)
            		{
            			Intent intent = new Intent(DisplaySize.this, FullTMD.class);
            			intent.putExtra("brands", brands);
            			startActivity(intent);
            		}
            		else
            		{
            			Intent intent = new Intent(DisplaySize.this, LoproTMD.class);
        				intent.putExtra("brands", brands);
            			startActivity(intent);
            		}
            	}
            }
		});
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
			startActivity(new Intent(DisplaySize.this, TMDmenu.class));
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
