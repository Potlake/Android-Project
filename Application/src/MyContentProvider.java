package com.delivery.assistant;

import static com.delivery.assistant.Constants.TABLE_NAME;
import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.AUTHORITY;
import static com.delivery.assistant.Constants.CONTENT_TYPE;
import static com.delivery.assistant.Constants.CONTENT_ITEM_TYPE;
import static com.delivery.assistant.Constants.CONTENT_URI;

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

      if (uriMatcher.match(uri) == EVENTS_ID) {
         long id = Long.parseLong(uri.getPathSegments().get(1));
         selection = appendRowId(selection, id);
      }

      // Get the database and run the query
      SQLiteDatabase db = delivery.getReadableDatabase();
      Cursor cursor = db.query(TABLE_NAME, projection, selection,
            selectionArgs, null, null, orderBy);

      // Tell the cursor what uri to watch, so it knows when its
      // source data changes
      cursor.setNotificationUri(getContext().getContentResolver(),
            uri);
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
      SQLiteDatabase db = delivery.getWritableDatabase();

      // Validate the requested uri
      if (uriMatcher.match(uri) != EVENTS) {
         throw new IllegalArgumentException("Unknown URI " + uri);
      }

      // Insert into database
      long id = db.insertOrThrow(TABLE_NAME, null, values);

      // Notify any watchers of the change
      Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
      getContext().getContentResolver().notifyChange(newUri, null);
      return newUri;
   }
   
   @Override
   public int delete(Uri uri, String selection,
         String[] selectionArgs) {
      SQLiteDatabase db = delivery.getWritableDatabase();
      int count;
      switch (uriMatcher.match(uri)) {
      case EVENTS:
         count = db.delete(TABLE_NAME, selection, selectionArgs);
         break;
      case EVENTS_ID:
         long id = Long.parseLong(uri.getPathSegments().get(1));
         count = db.delete(TABLE_NAME, appendRowId(selection, id),
               selectionArgs);
         break;
      default:
         throw new IllegalArgumentException("Unknown URI " + uri);
      }

      // Notify any watchers of the change
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }
   
   @Override
   public int update(Uri uri, ContentValues values,
         String selection, String[] selectionArgs) {
      SQLiteDatabase db = delivery.getWritableDatabase();
      int count;
      switch (uriMatcher.match(uri)) {
      case EVENTS:
         count = db.update(TABLE_NAME, values, selection,
               selectionArgs);
         break;
      case EVENTS_ID:
         long id = Long.parseLong(uri.getPathSegments().get(1));
         count = db.update(TABLE_NAME, values, appendRowId(
               selection, id), selectionArgs);
         break;
      default:
         throw new IllegalArgumentException("Unknown URI " + uri);
      }

      // Notify any watchers of the change
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }
   
   // Append an id test to a SQL selection expression
   private String appendRowId(String selection, long id) {
      return CO_ID + "=" + id 
	  + (!TextUtils.isEmpty(selection) 
		  ? " AND (" + selection + ')' 
		  : "");
   }
}
