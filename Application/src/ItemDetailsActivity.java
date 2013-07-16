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
    private String address;
    private String phone_number;
    private String receiver;

    private Uri detailUri;

    private String latitude;
    private String longitude;

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
			new getPositionFromAddress().execute();
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

	    receiver = cursor.getString(
		    cursor.getColumnIndexOrThrow(CO_RECEIVER));
	    Receiver.setText(receiver);

	    phone_number = cursor.getString(
		    cursor.getColumnIndexOrThrow(CO_NUMBER));
	    Number.setText(phone_number);

	    the_address = cursor.getString(
		    cursor.getColumnIndexOrThrow(CO_ADDRESS));
	    Address.setText(the_address);
	    address = the_address;

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

    // Search Location and get a (latitude, longitude) coordinate
    class getPositionFromAddress extends AsyncTask<String, String, String> {
	ProgressDialog pDialog;

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pDialog = new ProgressDialog(ItemDetailsActivity.this);
	    pDialog.setMessage("Getting Location. Please wait...");
	    pDialog.setIndeterminate(false);
	    pDialog.setCancelable(false);
	    pDialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
	    URL url;
	    the_address = the_address.replaceAll("\\s","+");
	    the_address = 
		"http://maps.googleapis.com/maps/api/geocode/json?address=" 
		+ the_address + "&sensor=true";
	    try {
		url = new URL(the_address);
		URLConnection connection = url.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader
			(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
		    builder.append(line);
		}
		JSONObject json = new JSONObject(builder.toString());
		try {
		    String status = json.getString("status");
		    if (status.equals("OK")) {
			try {
			    JSONArray results = json.getJSONArray("results");
			    JSONObject detail = results.getJSONObject(0);
			    JSONObject geometry = detail.getJSONObject("geometry");
			    JSONObject location = geometry.getJSONObject("location");
			    latitude = location.getString("lat");
			    longitude = location.getString("lng");
			} catch (JSONException e) {
			    throw new RuntimeException(e);
			}
		    }
		} catch (JSONException e) {
		    e.printStackTrace();
		}
	    } catch (MalformedURLException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	    return null;
	}

	@Override
	protected void onPostExecute(String file_url) {
	    pDialog.dismiss();
	    Intent i = new Intent(ItemDetailsActivity.this, ViewInMaps.class);
	    i.putExtra("latitude", latitude);
	    i.putExtra("longitude", longitude);
	    i.putExtra("address", address);
	    i.putExtra("receiver", receiver);
	    startActivity(i);
	}
    }
} 
