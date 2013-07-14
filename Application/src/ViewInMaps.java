package com.delivery.assistant;

public class ViewInMaps extends FragmentActivity
    implements ConnectionCallbacks, OnConnectionFailedListener,
	       LocationListener, OnMarkerClickListener {
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private String the_address;

    // For Setting Marker
    private static LatLng AddressLocation = new LatLng(45.05092,7.67832);
    private Marker AddressMarker;

    // for address location
    Geocoder myGeocoder;
    final static int MAX_RESULT = 1;

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
	    the_address = extras.getString("receiver_address");
	    // searchFromLocationName(the_address);
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
                .title("Brisbane")
                .snippet("Population: 2,074,200")
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
    
    // Search Location and get a (latitude, longitude) coordinate
    /*
    private void searchFromLocationName(String name) {
	try {
	    List<Address> result
		= myGeocoder.getFromLocationName(name, MAX_RESULT);
	    if ((result == null) || (result.isEmpty())) {
		Toast.makeText(getApplicationContext(),
			"Wrong address, Can not find this place!",
			Toast.LENGTH_LONG).show();
	    } else {
		double latitude = result.get(0).getLatitude();
		double longitude = result.get(0).getLongitude();
		// AddressLocation = new LatLng(latitude, longitude);
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	    Toast.makeText(getApplicationContext(),
		    "The network is unavailable!",
		    Toast.LENGTH_LONG).show();
	}
    }
    */

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
