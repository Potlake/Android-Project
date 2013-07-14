package com.delivery.assistant;

public class ViewInMaps extends FragmentActivity
    implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private LocationClient mLocationClient;

    // These settings are the same as the settings for the map.
    // They will in fact give you updates at the maximal rates 
    // currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
	.setInterval(5000) // 5 seconds
	.setFastestInterval(16) // 16ms = 60fps
	.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
	setUpLocationClientIfNeeded();
	mLocationClient.connect();
    }

    @Override
    public void onPause() {
	super.onPause();
	if (mLocationClient != null) {
	    mLocationClient.disconnect();
	}
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager()
		    .findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
		mMap.setMyLocationEnabled(true);
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
	if (mLocationClient == null) {
	    mLocationClient = new LocationClient(
		    getApplicationContext(),
		    this, //ConnectionCallbacks
		    this); // OnConnectionFailedListener
	}
    }

    // Button to get current Location
    public void showMyLocation(View view) {
	if (mLocationClient != null && mLocationClient.isConnected()) {
	    String msg = "Location = " + mLocationClient.getLastLocation();
	    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
    }

    // Implementation of LocationListener
    @Override
    public void onLocationChanged(Location location) {
	// Do nothing
    }

    // Callback called when connect to GCore. Implementation of ConnectionCallbacks
    @Override
    public void onConnected(Bundle connectionHint) {
	mLocationClient.requestLocationUpdates(
		REQUEST,
		this); // LocationListener
    }

    // Callback called when disconneted from GCore.
    // Implementation of ConnectionCallbacks
    @Override
    public void onDisconnected() {
	// Do nothing
    }

    // Implementation of OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult result) {
	// Do nothing
    }
}

