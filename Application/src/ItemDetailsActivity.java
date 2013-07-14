package com.delivery.assistant;

import static com.delivery.assistant.Constants.CONTENT_ITEM_TYPE;
import static com.delivery.assistant.Constants.CONTENT_URI;
import static com.delivery.assistant.Constants.CO_ID;
import static com.delivery.assistant.Constants.CO_NAME;
import static com.delivery.assistant.Constants.CO_RECEIVER;
import static com.delivery.assistant.Constants.CO_NUMBER;
import static com.delivery.assistant.Constants.CO_ADDRESS;
import static com.delivery.assistant.Constants.CO_TIME;
import static com.delivery.assistant.Constants.CO_COMPLETED;
import static com.delivery.assistant.Constants.CO_FLAG;

public class ItemDetailsActivity extends Activity {

    private Spinner spinner;
    private TextView Name;
    private TextView Receiver;
    private TextView Number;
    private TextView Address;
    private TextView Time;
    private String Flag;
    private String state;
    private String the_address;
    private String phone_number;

    private Uri detailUri;

    @Override
    protected void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	setContentView(R.layout.details);

	spinner = (Spinner) findViewById(R.id.detail_completed);
	Name = (TextView) findViewById(R.id.detail_name);
	Receiver = (TextView) findViewById(R.id.detail_receiver);
	Number = (TextView) findViewById(R.id.detail_number);
	Address = (TextView) findViewById(R.id.detail_address);
	Time = (TextView) findViewById(R.id.detail_time);
	Button confirmButton = (Button) findViewById(R.id.confirmButton);
	Button mapViewButton = (Button) findViewById(R.id.mapViewButton);
	Button callButton = (Button) findViewById(R.id.callButton);

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

	// add PhoneStateListener
	PhoneCallListener phoneListener = new PhoneCallListener();
	TelephonyManager telephonyManager = (TelephonyManager) this
		.getSystemService(Context.TELEPHONY_SERVICE);
	telephonyManager.listen(phoneListener,
		PhoneStateListener.LISTEN_CALL_STATE);
	
	OnClickListener ButtonListener = new OnClickListener() {
	    public void onClick(View v) {
		switch(v.getId()) {
		    case R.id.confirmButton:
			// Return data back to its parent
			setResult(RESULT_OK);
			finish();
			break;
		    case R.id.mapViewButton:
			Intent i = new Intent(getApplicationContext(), ViewInMaps.class);
			i.putExtra("receiver_address", the_address);
			startActivity(i);
			break;
		    case R.id.callButton:
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phone_number));
			startActivity(callIntent);
		}
	    }
	};

	confirmButton.setOnClickListener(ButtonListener);
	mapViewButton.setOnClickListener(ButtonListener);
	callButton.setOnClickListener(ButtonListener);
    }

    private void fillData(Uri uri) {
	String[] projection = { CO_NAME, CO_RECEIVER, CO_NUMBER, CO_ADDRESS,
	    CO_TIME, CO_COMPLETED, CO_FLAG }; // Select Columns
	Cursor cursor = getContentResolver().query(
		uri, projection, null, null, null);
	if (cursor != null) {
	    cursor.moveToFirst();
	    state = cursor.getString(cursor.getColumnIndexOrThrow(CO_COMPLETED));
	    for (int i = 0; i < spinner.getCount(); i++) {
	        String s = (String) spinner.getItemAtPosition(i);
	        if (s.equalsIgnoreCase(state)) {
		    spinner.setSelection(i);
	        }
	    }
	    Name.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_NAME)));
	    Receiver.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_RECEIVER)));
	    Number.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_NUMBER)));
	    phone_number = cursor.getString(
		    cursor.getColumnIndexOrThrow(CO_NUMBER));
	    Address.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_ADDRESS)));
	    the_address = cursor.getString(
		    cursor.getColumnIndexOrThrow(CO_ADDRESS));
	    Time.setText(cursor.getString(
	    	    cursor.getColumnIndexOrThrow(CO_TIME)));
	    Flag = cursor.getString(
		    cursor.getColumnIndexOrThrow(CO_FLAG));
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
	String completed = (String) spinner.getSelectedItem();
	if ( !completed.equals(state) ) {
	    Flag = "1";
	}
	String name = Name.getText().toString();
	String receiver = Receiver.getText().toString();
	String number = Number.getText().toString();
	String address = Address.getText().toString();
	String time = Time.getText().toString();

	ContentValues values = new ContentValues();
	values.put(CO_COMPLETED, completed);
	values.put(CO_NAME, name);
	values.put(CO_RECEIVER, receiver);
	values.put(CO_NUMBER, number);
	values.put(CO_ADDRESS, address);
	values.put(CO_TIME, time);
	values.put(CO_FLAG, Flag);

	if (detailUri == null) {
	    detailUri = getContentResolver().insert(CONTENT_URI, values);
	} else {
	    getContentResolver().update(detailUri, values, null, null);
	}
    }

    private class PhoneCallListener extends PhoneStateListener {
	private boolean isPhoneCalling = false;
	String LogTag = "PHONE CALL LOGGING";
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
	    if (TelephonyManager.CALL_STATE_RINGING == state) {
		Log.i(LogTag, "RINGING, number: " + incomingNumber);
	    }
	    if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
		Log.i(LogTag, "OFFHOOK");
		isPhoneCalling = true;
	    }
	    if (TelephonyManager.CALL_STATE_IDLE == state) {
		// run when class initial and phone call ended,
		// need detect flag from CALL_STATE_OFFHOOK
		Log.i(LogTag, "IDLE");
		if (isPhoneCalling) {
		    Log.i(LogTag, "restart app");
		    Intent i = getBaseContext().getPackageManager()
			.getLaunchIntentForPackage(
				getBaseContext().getPackageName());
		    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(i);
		    isPhoneCalling = false;
		}
	    }
	}
    }
} 
