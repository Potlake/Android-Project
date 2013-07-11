package com.delivery.assistant;

import static com.delivery.assistant.Constants.CONTENT_ITEM_TYPE;
import static com.delivery.assistant.Constants.CONTENT_URI;
import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.CO_NAME;
import static com.delivery.assistant.Constants.CO_RECEIVER;
import static com.delivery.assistant.Constants.CO_ADDRESS;
import static com.delivery.assistant.Constants.CO_COMPLETION;

public class ItemDetailsActivity extends Activity {

    private Spinner iCompletion;
    private TextView iName;
    private TextView iReceiver;
    private TextView iAddress;

    private Uri detailUri;

    @Override
    protected void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	setContentView(R.layout.details);

	iCompletion = (Spinner) findViewById(R.id.detail_completion);
	iName = (TextView) findViewById(R.id.detail_name);
	iReceiver = (TextView) findViewById(R.id.detail_receiver);
	iAddress = (TextView) findViewById(R.id.detail_address);
	Button confirmButton = (Button) findViewById(R.id.confirmButton);

	Bundle extras = getIntent().getExtras();

	// Check from the saved Instance
	detailUri = (bundle == null) ? null : (Uri) bundle
	    .getParcelable(CONTENT_ITEM_TYPE);

	// Or passed from the other activity
	if (extras != null) {
	    detailUri = extras
		.getParcelable(CONTENT_ITEM_TYPE);
	    fillData(detailUri);
	}
	
	confirmButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View view) {
		setResult(RESULT_OK); // Return data back to its parent
		finish();
	    }
	});
    }

    private void fillData(Uri uri) {
	String[] projection = { CO_ID, CO_NAME, CO_RECEIVER, CO_ADDRESS,
	    CO_COMPLETION }; // Select Columns
	Cursor cursor = getContentResolver().query(
		uri, projection, null, null, null);
	if (cursor != null){
	    cursor.moveToFirst();
	    String c = cursor.getString(cursor
		    .getColumnIndexOrThrow(CO_COMPLETION));
	    for (int i = 0; i < iCompletion.getCount(); i++) {
	        String s = (String) iCompletion.getItemAtPosition(i);
	        if (s.equalsIgnoreCase(c)) {
	    	iCompletion.setSelection(i);
	        }
	    }
	    iName.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_NAME)));
	    iReceiver.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_RECEIVER)));
	    iAddress.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_ADDRESS)));
	    cursor.close();
	}
    }

    protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	saveState();
	outState.putParcelable(CONTENT_ITEM_TYPE, detailUri);
    }

    @Override
    protected void onPause() {
	super.onPause();
	saveState();
    }

    private void saveState() {
	String completion = (String) iCompletion.getSelectedItem();
	String name = iName.getText().toString();
	String receiver = iReceiver.getText().toString();
	String address = iAddress.getText().toString();

	ContentValues values = new ContentValues();
	values.put(CO_COMPLETION, completion);
	values.put(CO_NAME, name);
	values.put(CO_RECEIVER, receiver);
	values.put(CO_ADDRESS, address);

	if (detailUri == null) {
	    detailUri = getContentResolver().insert(CONTENT_URI, values);
	} else {
	    getContentResolver().update(detailUri, values, null, null);
	}
    }
} 
