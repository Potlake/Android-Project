package com.delivery.assistant;

import static com.delivery.assistant.Constants.CO_ADDRESS;
import static com.delivery.assistant.Constants.CO_COMPLETED;
import static com.delivery.assistant.Constants.CO_FLAG;
import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.CO_NAME;
import static com.delivery.assistant.Constants.CO_NUMBER;
import static com.delivery.assistant.Constants.CO_RECEIVER;
import static com.delivery.assistant.Constants.CO_TIME;
import static com.delivery.assistant.Constants.TABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "delivery.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " 
        + TABLE_NAME
        + "("
        + CO_ID + " integer primary key autoincrement, " 
        + CO_NAME + " text not null, " 
        + CO_RECEIVER + " text not null, " 
        + CO_NUMBER + " text not null, " 
        + CO_ADDRESS + " text not null, " 
        + CO_TIME + " text not null, " 
        + CO_COMPLETED + " text not null, "
        + CO_FLAG + " text not null"
        + ");";

   // Create a helper object for the Events database
    public DBHelper(Context ctx) {
	super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

   @Override
   public void onCreate(SQLiteDatabase db) {
	db.execSQL(DATABASE_CREATE);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion,
         int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(db);
   }
}
