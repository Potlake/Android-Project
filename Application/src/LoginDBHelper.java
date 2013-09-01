package com.delivery.assistant;

import static com.delivery.assistant.Constants.CO_ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "login.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " 
        + "LOGIN"
        + "("
        + CO_ID + " integer primary key autoincrement, " 
        + "username" + " text not null, " 
        + "password" + " text not null" 
        + ");";

   // Create a helper object for the Events database
    public LoginDBHelper(Context ctx) {
	super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

   @Override
   public void onCreate(SQLiteDatabase db) {
	db.execSQL(DATABASE_CREATE);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion,
         int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + "LOGIN");
      onCreate(db);
   }
}
