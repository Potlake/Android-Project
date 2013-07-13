package com.delivery.assistant;

// All the Constants
import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.CO_NAME;
import static com.delivery.assistant.Constants.CO_RECEIVER;
import static com.delivery.assistant.Constants.CO_NUMBER;
import static com.delivery.assistant.Constants.CO_ADDRESS;
import static com.delivery.assistant.Constants.CO_TIME;
import static com.delivery.assistant.Constants.CO_COMPLETED;
import static com.delivery.assistant.Constants.CO_FLAG;

import static com.delivery.assistant.Constants.TAG_PRODUCTS;
import static com.delivery.assistant.Constants.TAG_SUCCESS;
import static com.delivery.assistant.Constants.TAG_ID;
import static com.delivery.assistant.Constants.TAG_NAME;
import static com.delivery.assistant.Constants.TAG_RECEIVER;
import static com.delivery.assistant.Constants.TAG_NUMBER;
import static com.delivery.assistant.Constants.TAG_ADDRESS;
import static com.delivery.assistant.Constants.TAG_TIME;
import static com.delivery.assistant.Constants.TAG_COMPLETED;

import static com.delivery.assistant.Constants.URL_ALL_PRODUCTS;
import static com.delivery.assistant.Constants.CONTENT_URI;
import static com.delivery.assistant.Constants.CONTENT_ITEM_TYPE;

public class Assistant extends ListActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
    private static String[] FROM = {CO_ID, CO_NAME, 
	CO_RECEIVER, CO_TIME, CO_COMPLETED};
    private static int[] TO = {R.id.rowid, R.id.name, R.id.receiver,
	R.id.time, R.id.completed};
    private ProgressDialog pDialog; // Progress Dialog
    JSONParser jParser = new JSONParser(); // Creating JSON Parser object
    private SimpleCursorAdapter adapter;

    // Create Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	showItemList();
    }
    
    // Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.assistant, menu);
    	return true;
    }
    
    // Menu Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	    case R.id.sync:
		new syncAllProducts().execute();
		return true;
	    case R.id.about:
		clearAllItems();
		return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // When an entry is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	Intent i = new Intent(this, ItemDetailsActivity.class);
	Uri detailUri = Uri.parse(CONTENT_URI + "/" + id);
	i.putExtra(CONTENT_ITEM_TYPE, detailUri);
	startActivity(i);
    }

    // Show Item List
    private void showItemList() {
	getLoaderManager().initLoader(0, null, this);
	adapter = new SimpleCursorAdapter(this, R.layout.item,
		null, FROM, TO, 0);
        setListAdapter(adapter);
    }

    // Creates a new loader after the initLoader() call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	CursorLoader cursorLoader = new CursorLoader(this,
		CONTENT_URI, FROM, null, null, null);
	return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
	// When data is not available, delete reference
	adapter.swapCursor(null);
    }
    
    // Clear All Items
    private void clearAllItems() {
        getContentResolver().delete(CONTENT_URI, null, null);
    }

    class syncAllProducts extends AsyncTask<String, String, String> {
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pDialog = new ProgressDialog(Assistant.this);
	    pDialog.setMessage("Loading products. Please wait...");
	    pDialog.setIndeterminate(false);
	    pDialog.setCancelable(false);
	    pDialog.show();
	}
	@Override
	protected String doInBackground(String... args) {
	    // Building Parameters
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    // getting JSON string from URL
	    JSONObject json = jParser.makeHttpRequest(URL_ALL_PRODUCTS, "GET", params);
	    try {
		// Checking for SUCCESS TAG
		int success = json.getInt(TAG_SUCCESS);
		if (success == 1) // products found
		{
		    clearAllItems();
		    fillData(json);
		}
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	    return null;
    }
	protected void onPostExecute(String file_url) {
	    // dismiss the dialog after getting all products
	    pDialog.dismiss();
	}
    }

    // Put data into sqlite database
    private void fillData(JSONObject json) {
	// Getting Array of Products
	try {
	    JSONArray products = json.getJSONArray(TAG_PRODUCTS);
	    for (int i = 0; i < products.length(); i++) {
		JSONObject j = products.getJSONObject(i);
		// Storing each json item in variable
		String name = j.getString(TAG_NAME);
	    	String receiver = j.getString(TAG_RECEIVER);
	    	String number = j.getString(TAG_NUMBER);
	    	String address = j.getString(TAG_ADDRESS);
	    	String time = j.getString(TAG_TIME);
	    	String completed = j.getString(TAG_COMPLETED);
	    	// Adding them to database
	    	ContentValues values = new ContentValues();
	    	values.put(CO_NAME, name);
	    	values.put(CO_RECEIVER, receiver);
	    	values.put(CO_NUMBER, number);
	    	values.put(CO_ADDRESS, address);
	    	values.put(CO_TIME, time);
	    	values.put(CO_COMPLETED, completed);
	    	values.put(CO_FLAG, "0");
	    	getContentResolver().insert(CONTENT_URI, values);
	    }
	} catch (JSONException e) {
	    throw new RuntimeException(e);
	}
    }
}
