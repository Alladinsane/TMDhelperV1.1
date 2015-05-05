package com.example.tmdhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NumberPickerActivity extends MainActivity implements OnClickListener
{
	android.widget.NumberPicker np;
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.number_picker);
	  ActionBar actionBar = getSupportActionBar();
	  actionBar.hide();
	  np = (android.widget.NumberPicker)findViewById(R.id.number_picker);
	  int[] resources = {R.id.apply_button};
	  for (int i=0; i <resources.length; i++)
	  {
		  Button b = (Button)findViewById(resources[i]);
		  b.setOnClickListener(this);
	  }
	  int max = getIntent().getIntExtra("maxValue", 1);
	  np.setMinValue(1);// restricted number to minimum value i.e 1
	  np.setMaxValue(max);// restricted number to maximum value 
	  np.setWrapSelectorWheel(true); 

	  np.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() 
	  {

	@Override
	public void onValueChange(android.widget.NumberPicker picker, int oldVal,
			int newVal) {
		// TODO Auto-generated method stub
		 String Old = "Old Value : ";

	       String New = "New Value : ";

	}
    });

     Log.d("NumberPicker", "NumberPicker");

   }

@Override
public void onClick(View v) {
	int index = v.getId(); 
	
	if(index==R.id.apply_button)
	{
		int value = np.getValue();
		
		Log.d("Mine", "value = " +value);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", value);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}

}/* NumberPickerActivity */