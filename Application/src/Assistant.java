package com.delivery.assistant;

import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.CO_NAME;
import static com.delivery.assistant.Constants.CO_RECEIVER;
import static com.delivery.assistant.Constants.CO_ADDRESS;
import static com.delivery.assistant.Constants.CO_COMPLETION;
import static com.delivery.assistant.Constants.TAG_PRODUCTS;
import static com.delivery.assistant.Constants.TAG_SUCCESS;
import static com.delivery.assistant.Constants.TAG_ID;
import static com.delivery.assistant.Constants.TAG_NAME;
import static com.delivery.assistant.Constants.TAG_RECEIVER;
import static com.delivery.assistant.Constants.TAG_ADDRESS;
import static com.delivery.assistant.Constants.TAG_COMPLETION;
import static com.delivery.assistant.Constants.URL_ALL_PRODUCTS;
import static com.delivery.assistant.Constants.CONTENT_URI;
import static com.delivery.assistant.Constants.CONTENT_ITEM_TYPE;

public class Assistant extends ListActivity {
    private static String[] FROM = {CO_ID, CO_NAME, CO_RECEIVER, CO_COMPLETION};
    private static int[] TO = {R.id.rowid, R.id.name, R.id.receiver, R.id.completion};
    private ProgressDialog pDialog; // Progress Dialog
    JSONParser jParser = new JSONParser(); // Creating JSON Parser object

    // Create Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
	addNewItem("Banana", "YU ZIJIE");
	addNewItem("Apple", "LIU YAOPENG");
	addNewItem("Orange", "LI CHANG");
	addNewItem("Pear", "YANG LIXIAN");
    	Cursor cursor = getItemList();
    	showItemList(cursor);
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
    	case R.id.clear:
    	    clearAllItems();
    	    return true;
    	case R.id.sync:
	    new LoadAllProducts().execute();
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

    // Add New Item
    private void addNewItem(String name, String receiver) {
	ContentValues values = new ContentValues();
	values.put("name", name);
	values.put("receiver", receiver);
	values.put("completion", "Not completed");
	values.put("address", "0");
	getContentResolver().insert(CONTENT_URI, values);
    }
    
    // Get Item List
    private Cursor getItemList() {
        return managedQuery(CONTENT_URI, FROM, null, null, null);
    }
    
    // Show Item List
    private void showItemList(Cursor cursor) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
    	    this, R.layout.item, cursor, FROM, TO);
        setListAdapter(adapter);
    }
    
    // Clear All Items
    private void clearAllItems() {
        getContentResolver().delete(CONTENT_URI, null, null);
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
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
		    // Getting Array of Products
		    JSONArray products = json.getJSONArray(TAG_PRODUCTS);
		    for (int i = 0; i < products.length(); i++) {
		    	JSONObject j = products.getJSONObject(i);
		    	// Storing each json item in variable
		    	String name = j.getString(TAG_NAME);
		    	String receiver = j.getString(TAG_RECEIVER);
		    	String address = j.getString(TAG_ADDRESS);
		    	String completion = j.getString(TAG_COMPLETION);
			// Adding them to database
			ContentValues values = new ContentValues();
			values.put(CO_NAME, name);
			values.put(CO_RECEIVER, receiver);
			values.put(CO_ADDRESS, address);
			values.put(CO_COMPLETION, completion);
			getContentResolver().insert(CONTENT_URI, values);
		    }
		}
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	    return null;
    }
	protected void onPostExecute(String file_url) {
	    // dismiss the dialog after getting all products
	    pDialog.dismiss();
	    // updating UI from Background Thread
	    runOnUiThread(new Runnable() {
	    	public void run() {
		    Cursor cursor = getItemList();
		    showItemList(cursor);
	    	}
	    });
	}
    }
}
