package com.example.tmdhelper;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MyDatabaseAdapter {
	
	MySQLiteOpenHelper myHelper;
	
	public MyDatabaseAdapter(Context context)
	{
		myHelper = new MySQLiteOpenHelper(context);
	}
	
	public long insertDataProducts(String item, String color, 
			int caseCount, int shelfCount)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteOpenHelper.NAME, item);
		contentValues.put(MySQLiteOpenHelper.COLOR, color);
		contentValues.put(MySQLiteOpenHelper.CASE_COUNT, caseCount);
	    contentValues.put(MySQLiteOpenHelper.SHELF_COUNT, shelfCount);
	    
	    long id = db.insert(MySQLiteOpenHelper.TABLE_NAME_PRODUCTS, null, contentValues);
	    
	    return id;
	}
	public long insertDataTMDs(String name, String shelf1, String shelf2, String shelf3,
			String shelf4, String shelf5)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteOpenHelper.NAME, name);
		contentValues.put(MySQLiteOpenHelper.SHELF_1, shelf1);
		contentValues.put(MySQLiteOpenHelper.SHELF_2, shelf2);
		contentValues.put(MySQLiteOpenHelper.SHELF_3, shelf3);
		contentValues.put(MySQLiteOpenHelper.SHELF_4, shelf4);
		contentValues.put(MySQLiteOpenHelper.SHELF_5, shelf5);
	    
	    long id = db.insert(MySQLiteOpenHelper.TABLE_NAME_TMDS, null, contentValues);
	    
	    return id;
	}
	public String getProductData(String name)
	{
		SQLiteDatabase db = myHelper.getReadableDatabase();
		String[] columns = {MySQLiteOpenHelper.NAME, 
				MySQLiteOpenHelper.COLOR, MySQLiteOpenHelper.CASE_COUNT, 
				MySQLiteOpenHelper.SHELF_COUNT};
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_NAME_PRODUCTS, columns,  
				MySQLiteOpenHelper.NAME + " = '" + name + "'", null, null, null, null);
		StringBuffer buffer = new StringBuffer();
		while(cursor.moveToNext()){
			int index1 = cursor.getColumnIndex(MySQLiteOpenHelper.NAME);
			int index2 = cursor.getColumnIndex(MySQLiteOpenHelper.COLOR);
			int index3 = cursor.getColumnIndex(MySQLiteOpenHelper.CASE_COUNT);
			int index4 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_COUNT);
			String brandName = cursor.getString(index1);
			String color = cursor.getString(index2);
			int caseCount = cursor.getInt(index3);
			int shelfCount = cursor.getInt(index4);
			buffer.append(brandName + " " + color + " " + caseCount + " " + shelfCount + "\n");
		}
		return buffer.toString();
	}
	public String getPlanogramData(String name)
	{
		SQLiteDatabase db = myHelper.getReadableDatabase();
		String[] columns = {MySQLiteOpenHelper.NAME, 
				MySQLiteOpenHelper.SHELF_1, MySQLiteOpenHelper.SHELF_2, 
				MySQLiteOpenHelper.SHELF_3, MySQLiteOpenHelper.SHELF_4, 
				MySQLiteOpenHelper.SHELF_5};
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_NAME_TMDS, columns,  
				MySQLiteOpenHelper.NAME + " = '" + name + "'", null, null, null, null);
		StringBuffer buffer = new StringBuffer();
		while(cursor.moveToNext()){
			int index1 = cursor.getColumnIndex(MySQLiteOpenHelper.NAME);
			int index2 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_1);
			int index3 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_2);
			int index4 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_3);
			int index5 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_4);
			int index6 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_5);
			
			String tmdName = cursor.getString(index1);
			String shelf1 = cursor.getString(index2);
			String shelf2 = cursor.getString(index3);
			String shelf3 = cursor.getString(index4);
			String shelf4 = cursor.getString(index5);
			String shelf5 = cursor.getString(index6);
			
			buffer.append(tmdName + " " + shelf1 + " " + shelf2 + " " + shelf3 + " " + 
			shelf4 + " " + shelf5+ "\n");
		}
		return buffer.toString();
	}
	public ArrayList<String> getAllItems() {
		ArrayList<String> allItems= new ArrayList<String>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
		String[] columns = {MySQLiteOpenHelper.NAME, 
				MySQLiteOpenHelper.SHELF_1, MySQLiteOpenHelper.SHELF_2, 
				MySQLiteOpenHelper.SHELF_3, MySQLiteOpenHelper.SHELF_4, 
				MySQLiteOpenHelper.SHELF_5};
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_NAME_TMDS, columns,  
				null, null, null, null, null);
		while(cursor.moveToNext()){
			int index2 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_1);
			int index3 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_2);
			int index4 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_3);
			int index5 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_4);
			int index6 = cursor.getColumnIndex(MySQLiteOpenHelper.SHELF_5);
			
			String shelf1 = cursor.getString(index2);
			String shelf2 = cursor.getString(index3);
			String shelf3 = cursor.getString(index4);
			String shelf4 = cursor.getString(index5);
			String shelf5 = cursor.getString(index6);
			
			allItems.add(shelf1);
			allItems.add(shelf2);
			allItems.add(shelf3);
			allItems.add(shelf4);
			allItems.add(shelf5);
		}
		return allItems;
	}
	public void deleteTMDdatabase()
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		db.execSQL("delete from "+ MySQLiteOpenHelper.TABLE_NAME_TMDS);
	}
	
	static class MySQLiteOpenHelper extends SQLiteOpenHelper{
		private static final String DATABASE_NAME = "MyDatabase";
		private static final String TABLE_NAME_PRODUCTS = "Products_table";
		private static final String TABLE_NAME_TMDS = "TMDs_Table";
		private static final String UID = "_id";
		private static final String NAME = "name";
		private static final String COLOR = "color";
		private static final String CASE_COUNT = "caseCount";
		private static final String SHELF_COUNT = "shelfCount";
		private static final String SHELF_1 = "Shelf1";
		private static final String SHELF_2 = "Shelf2";
		private static final String SHELF_3 = "Shelf3";
		private static final String SHELF_4 = "Shelf4";
		private static final String SHELF_5 = "Shelf5";
		private static final int DATABASE_VERSION = 1;
		private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE "+ TABLE_NAME_PRODUCTS +
				" ( "+ UID +" INTEGER_PRIMARY_KEY, " + NAME +" VARCHAR(255), " +
						COLOR + " VARCHAR(255), " + CASE_COUNT + " VARCHAR(255), " + SHELF_COUNT +
						" VARCHAR(255)" + " ) ";
		private static final String CREATE_TABLE_TMDS = "CREATE TABLE "+ TABLE_NAME_TMDS +
				" ( "+ UID +" INTEGER_PRIMARY_KEY, " + NAME +" VARCHAR(255), " +
						SHELF_1 + " VARCHAR(255), " + SHELF_2 + " VARCHAR(255), "+ 
				SHELF_3 + " VARCHAR(255), "+ SHELF_4+ " VARCHAR(255), "+SHELF_5 +" ) ";
		private static final String DROP_TABLE1 = "DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCTS;
		private static final String DROP_TABLE2 = "DROP TABLE IF EXISTS " + TABLE_NAME_TMDS;
		private Context context;
		MySQLiteOpenHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
			Log.d("Mine", "Constructor was called");
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			Log.d("Mine", "OnCreate was called");
				try {
					db.execSQL(CREATE_TABLE_PRODUCTS);
					db.execSQL(CREATE_TABLE_TMDS);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("Mine", "onUpgrade was called");
			try {
				db.execSQL(DROP_TABLE1);
				db.execSQL(DROP_TABLE2);
				onCreate(db);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	

}
