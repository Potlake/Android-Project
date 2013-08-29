package com.delivery.assistant;

import static com.delivery.assistant.Constants.AUTHORITY;
import static com.delivery.assistant.Constants.CONTENT_ITEM_TYPE;
import static com.delivery.assistant.Constants.CONTENT_TYPE;
import static com.delivery.assistant.Constants.CONTENT_URI;
import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.TABLE_NAME;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {
   private static final int EVENTS = 1;
   private static final int EVENTS_ID = 2;

   private DBHelper delivery;
   private UriMatcher uriMatcher;

   @Override
   public boolean onCreate() {
      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      uriMatcher.addURI(AUTHORITY, TABLE_NAME, EVENTS);
      uriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", EVENTS_ID);
      delivery = new DBHelper(getContext()); // setup new database
      return true;
   }
   
   @Override
   public Cursor query(Uri uri, String[] projection,
         String selection, String[] selectionArgs, String orderBy) {

       // Using SQLiteQueryBuilder
       SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

       // Set the table
       queryBuilder.setTables(TABLE_NAME);

      int uriType = uriMatcher.match(uri);
      switch (uriType) {
	  case EVENTS:
	      break;
	  case EVENTS_ID:
	      // Adding the ID to the original query
	      queryBuilder.appendWhere(CO_ID + "="
		      + uri.getLastPathSegment());
	      break;
	  default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
      }

      // Get the database and run the query
      SQLiteDatabase db = delivery.getReadableDatabase();
      Cursor cursor = queryBuilder.query(db, projection, selection,
	      selectionArgs, null, null, orderBy);

      // Tell the cursor what uri to watch, so it knows when its
      // source data changes
      cursor.setNotificationUri(getContext().getContentResolver(), uri);
      return cursor;
   }
   
   @Override
   public String getType(Uri uri) {
      switch (uriMatcher.match(uri)) {
      case EVENTS:
         return CONTENT_TYPE;
      case EVENTS_ID:
         return CONTENT_ITEM_TYPE;
      default:
         throw new IllegalArgumentException("Unknown URI " + uri);
      }
   }
   
   @Override
   public Uri insert(Uri uri, ContentValues values) {
	int uriType = uriMatcher.match(uri);
	SQLiteDatabase db = delivery.getWritableDatabase();
	long id = 0;
	switch (uriType) {
	    case EVENTS:
		id = db.insertOrThrow(TABLE_NAME, null, values);
		break;
	    default:
		throw new IllegalArgumentException("Unknown URI " 
			+ uri);
	}

      // Notify any watchers of the change
      Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
      getContext().getContentResolver().notifyChange(newUri, null);
      return newUri;
   }
   
    @Override
    public int delete(Uri uri, String selection,
         String[] selectionArgs) {
	int uriType = uriMatcher.match(uri);
	SQLiteDatabase db = delivery.getWritableDatabase();
	int rowsDeleted = 0;
	switch (uriType) {
	case EVENTS:
	    rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
	    break;
	case EVENTS_ID:
	    String id = uri.getLastPathSegment();
	    if (TextUtils.isEmpty(selection)) {
		rowsDeleted = db.delete(TABLE_NAME,
			CO_ID + "=" + id, null);
	    } else {
		rowsDeleted = db.delete(TABLE_NAME,
			CO_ID + "=" + id + " and " + selection,
			selectionArgs);
	    }
	    break;
	default:
	    throw new IllegalArgumentException("Unknown URI " + uri);
	}

	// Notify any watchers of the change
	getContext().getContentResolver().notifyChange(uri, null);
	return rowsDeleted;
    }
   
    @Override
    public int update(Uri uri, ContentValues values, String selection,
	   String[] selectionArgs) {
	int uriType = uriMatcher.match(uri);
	SQLiteDatabase db = delivery.getWritableDatabase();
	int rowsUpdated = 0;
	switch (uriType) {
	case EVENTS:
	    rowsUpdated = db.update(TABLE_NAME, values, selection,
		    selectionArgs);
	    break;
	case EVENTS_ID:
	    String id = uri.getLastPathSegment();
	    if (TextUtils.isEmpty(selection)) {
		rowsUpdated = db.update(TABLE_NAME, values,
			CO_ID + "=" + id, null);
	    } else {
		rowsUpdated = db.update(TABLE_NAME, values,
			CO_ID + "=" + id + " and " + selection,
			selectionArgs);
	    }
	    break;
	default:
	    throw new IllegalArgumentException("Unknown URI " + uri);
	}
	
	// Notify any watchers of the change
	getContext().getContentResolver().notifyChange(uri, null);
	return rowsUpdated;
   }
}
