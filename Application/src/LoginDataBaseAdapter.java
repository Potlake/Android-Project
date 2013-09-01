package com.delivery.assistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LoginDataBaseAdapter {
	// Variable to hold the database instance
	public  SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private LoginDBHelper dbHelper;
	
	public  LoginDataBaseAdapter(Context _context) 
	{
		context = _context;
		dbHelper = new LoginDBHelper(context);
	}
	public  LoginDataBaseAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	public void close() 
	{
		db.close();
	}

	public  SQLiteDatabase getDatabaseInstance()
	{
		return db;
	}

	public void insertEntry(String username,String password)
	{
	    ContentValues newValues = new ContentValues();
	    // Assign values for each row.
	    newValues.put("username", username);
	    newValues.put("password",password);
	    // Insert the row into your table
	    db.insert("LOGIN", null, newValues);
	}
	public int deleteEntry(String username)
	{
	    int numberOFEntriesDeleted= db.delete("LOGIN", " username=?",
		    new String[]{username}) ;
	    return numberOFEntriesDeleted;
	}	
	public String getSinlgeEntry(String username)
	{
	    Cursor cursor=db.query("LOGIN", null, " username=?",
		    new String[]{username}, null, null, null);
	    if(cursor.getCount()<1) {
        	cursor.close();
        	return "NOT EXIST";
	    } else {
		cursor.moveToFirst();
		String password= cursor.getString(cursor.getColumnIndex("password"));
		cursor.close();
		return password;				
	    }
	}
	
	public boolean existOrNot (String username)
	{
	    Cursor cursor=db.query("LOGIN", null, " username=?",
		    new String[]{username}, null, null, null);
	    if(cursor.getCount()<1){
        	cursor.close();
        	return false;
	    } else {
        	cursor.close();
        	return true;
	    }
	}
	
	public void updateEntry(String username,String password)
	{
	    ContentValues updatedValues = new ContentValues();
	    updatedValues.put("username", username);
	    updatedValues.put("password",password);
	    db.update("LOGIN", updatedValues, " username=?",
		    new String[]{username});			   
	}		
}
