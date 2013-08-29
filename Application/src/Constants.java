package com.delivery.assistant;

import android.net.Uri;

public interface Constants {

    // Table Names
    public static final String TABLE_NAME = "delivery";
    public static final String CO_ID = "_id";
    public static final String CO_NAME = "name";
    public static final String CO_RECEIVER = "receiver";
    public static final String CO_NUMBER = "number";
    public static final String CO_ADDRESS = "address";
    public static final String CO_TIME = "time";
    public static final String CO_COMPLETED = "completed";
    public static final String CO_FLAG = "flag";

    // JSON Node Names
    public static final String TAG_PRODUCTS = "products";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_ID = "pid";
    public static final String TAG_NAME = "name";
    public static final String TAG_RECEIVER = "receiver";
    public static final String TAG_NUMBER = "number";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_TIME = "time";
    public static final String TAG_COMPLETED = "completed";

    public static final String AUTHORITY = "com.delivery.assistant";
    public static final Uri CONTENT_URI = Uri.parse("content://"
         + AUTHORITY + "/" + TABLE_NAME);

    // The MIME type of a directory of events
    public static final String CONTENT_TYPE 
	= "vnd.android.cursor.dir/vnd.delivery.assistant";

    // The MIME type of a single event
    public static final String CONTENT_ITEM_TYPE
	= "vnd.android.cursor.item/vnd.delivery.assistant";

    // URLs
    public static final String URL_ALL_PRODUCTS
	= "http://yzj.name/all.php";
    public static final String URL_UPDATE_PRODUCTS
	= "http://yzj.name/update.php";
}
