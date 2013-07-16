package com.delivery.assistant;

public class ViewInMaps extends FragmentActivity
    implements ConnectionCallbacks, OnConnectionFailedListener,
	       LocationListener, OnMarkerClickListener {
    private GoogleMap mMap;
    private LocationClient mLocationClient;

    // For Setting Marker
    private static LatLng AddressLocation;
    private Marker AddressMarker;

    private String receiver;
    private String address;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

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

	Bundle extras = getIntent().getExtras();

	if (extras != null) {
	    Double latitude = Double.parseDouble(extras.getString("latitude"));
	    Double longitude = Double.parseDouble(extras.getString("longitude"));
	    receiver = extras.getString("receiver");
	    address = extras.getString("address");
	    AddressLocation = new LatLng(latitude, longitude);
	}
	setUpMapIfNeeded();
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
		setUpMap();
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

    // Add Location Markers
    private void setUpMap() {
	// Add lots of markers to the map
	addMarkersToMap();
	// Set listeners for marker events
	mMap.setOnMarkerClickListener(this);

	// Pan to see all markers in view
	final View mapView = getSupportFragmentManager()
	    .findFragmentById(R.id.map).getView();
	if (mapView.getViewTreeObserver().isAlive()) {
	    mapView.getViewTreeObserver().addOnGlobalLayoutListener(
		    new OnGlobalLayoutListener(){
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
			    LatLngBounds bounds = new LatLngBounds.Builder()
				    .include(AddressLocation)
				    .build();
		    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                      mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                      mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
			mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
		    }
		    });
	}

    }
    
    private void addMarkersToMap() {
	// Uses a colored icon
        AddressMarker = mMap.addMarker(new MarkerOptions()
                .position(AddressLocation)
                .title(receiver)
                .snippet(address)
                .icon(BitmapDescriptorFactory
		    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
    
    // Marker related Listeners

    @Override
    public boolean onMarkerClick(final Marker marker) {
	if (marker.equals(AddressMarker)) {
	    final Handler handler = new Handler();
	    final long start = SystemClock.uptimeMillis();
	    final long duration = 1500;

	    final Interpolator interpolator = new BounceInterpolator();

	    handler.post(new Runnable() {
		@Override
		public void run() {
		    long elapsed = SystemClock.uptimeMillis() - start;
		    float t = Math.max(1 - interpolator
			.getInterpolation((float) elapsed / duration), 0);
		    marker.setAnchor(0.5f, 1.0f + 2 * t);
		    if (t > 0.0) {
			handler.postDelayed(this, 16);
		    }
		}
	    });
	} 
	return false;
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
