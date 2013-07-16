package com.delivery.assistant;

import static com.delivery.assistant.Constants.CONTENT_URI;
import static com.delivery.assistant.Constants.CO_NAME;

public class SearchActivity extends Activity {

	private ListView lv;
	
	// Listview Adapter
	ArrayAdapter<String> adapter;
	
	// Search EditText
	EditText inputSearch;

	// ArrayList for Listview
	ArrayList<HashMap<String, String>> productList;

	List<String> products = new ArrayList<String>();

	private static String[] FROM = {CO_NAME};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

	Toast.makeText(getApplicationContext(),
		"We are sorry!, The Search Activity isn't good enough!",
		Toast.LENGTH_LONG).show();
	
	@SuppressWarnings("deprecation")
	Cursor cursor = managedQuery(CONTENT_URI, FROM, null, null, null);

	if (cursor != null) {
	    cursor.moveToFirst();
	    while (cursor.isAfterLast() == false) {
		products.add(cursor.getString(
			    cursor.getColumnIndexOrThrow(CO_NAME)));
		cursor.moveToNext();
	    }
	}

	lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        
        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.search_item, R.id.product_name, products);
        lv.setAdapter(adapter);

	inputSearch.addTextChangedListener(new TextWatcher() {
			
	@Override
	public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		// When user changed the Text
		SearchActivity.this.adapter.getFilter().filter(cs);	
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub							
	}

	});
    }
}
