package xchg.online.studenttrack;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xchg.online.baseframe.utils.Logger;
import org.xchg.online.baseframe.utils.SessionManager;
import org.xchg.online.baseframe.utils.Utilities;

import java.util.List;
import java.util.Set;

import xchg.online.studenttrack.data.LocationData;
import xchg.online.studenttrack.data.SessionData;
import xchg.online.studenttrack.data.TrackListener;
import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.data.TripSummary;
import xchg.online.studenttrack.utils.MapUtils;
import xchg.online.studenttrack.utils.OnRecordListener;
import xchg.online.studenttrack.utils.OnTripSaveListener;

public class TrackLocationActivity extends BaseLocationActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener, OnRecordListener, TravelData.OnServerCallListener, OnTripSaveListener, TrackListener {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private String TAG = TrackLocationActivity.class.getSimpleName();
    private static boolean startedTrip = false;
    private Button recordButton;
    private boolean firstload;
    private Marker currentMarker;


    public String getScreenName() { return this.getClass().getSimpleName(); }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem logout = menu.findItem(R.id.action_logout);
        logout.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_logout:
                SessionManager.logout(this);
                SessionData.reset(this);
                TravelData.resetTravels();
                Utilities.navigateToNextActivity(this, LoginActivity.class, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "Created TrackLocation");
        setContentView(R.layout.activity_track_location);
        setActionBar();

        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //removeSearchMenu();
        mActivity = this;
        //setActionBarTitle("Logout");
        TravelData.setCurrentActivity(this, this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        this.noRequests = -1;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.d(TAG, "Map is ready");
        if (googleMap == null) {
            Utilities.cancellableToast(getString(R.string.map_not_loaded_msg), this);
            return;
        }

        firstload = true;
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        LocationData current = TravelData.getCurrentLocation();


        if (current == null) {
            //Utilities.cancellableToast("Error loading current location. Please enable location.", this);
        } else {
            firstload = false;
            LatLng mCurrentLocation = new LatLng(current.getLatitude(), current.getLongitude());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f));

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .title("Current Location")
                    .snippet("")
                    .icon(createNormalMarkerView(R.color.grey_medium)));

            moveCurrentMarker(mCurrentLocation);
        }
        mGoogleMap.setOnMapLoadedCallback(this);

        recordButton = (Button) findViewById(R.id.btnRecordMap);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecord(!startedTrip);
            }
        });
        toggleRecord();

        List<LocationData> locations = TravelData.getCurrentRouteLocations();
        if (locations != null) {
            for (LocationData loc : locations) {
                //if (loc.isUnplotted()) {
                loc.setUnplotted(false);
                plotLocation(loc);
                //}
            }
        }
        //MapUtils.showRecordButton(this, true, this);

        //mGoogleMap.setOnInfoWindowClickListener(this);
    }

    private void moveCurrentMarker(LatLng mCurrentLocation) {

        if (currentMarker == null) {
            currentMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .title("Current Location")
                    .snippet("")
                    .icon(createLastMarker())
                    .zIndex(1.0f));
        } else {
            currentMarker.setPosition(mCurrentLocation);
        }
    }

    private BitmapDescriptor createNormalMarkerView(int color) {
        View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.location_marker, null);
        ImageView customerIcon = (ImageView) markerView.findViewById(R.id.customer_icon_iv);
        //customerIcon.setImageResource(R.drawable.circle_shape);
        GradientDrawable drawable = (GradientDrawable)getResources().getDrawable(R.drawable.circle_shape);
        drawable.setColor(getResources().getColor(color));
        customerIcon.setImageDrawable(drawable);
        return BitmapDescriptorFactory.fromBitmap(Utilities.createBitmapFromView(this, markerView));
    }

    private BitmapDescriptor createLastMarker() {
        View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.location_marker, null);
        ImageView customerIcon = (ImageView) markerView.findViewById(R.id.customer_icon_iv);
        customerIcon.setImageDrawable(getResources().getDrawable(R.drawable.current_location));
        return BitmapDescriptorFactory.fromBitmap(Utilities.createBitmapFromView(this, markerView));
    }

    private BitmapDescriptor createTrackedMarkerView() {
        /*View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.location_marker, null);
        ImageView customerIcon = (ImageView) markerView.findViewById(R.id.customer_icon_iv);
        customerIcon.setImageResource(R.drawable.tracked_circle);
        return BitmapDescriptorFactory.fromBitmap(Utilities.createBitmapFromView(this, markerView));*/
        return createNormalMarkerView(R.color.PaleVioletRed);
    }

    protected void handleLocationChange() {
        LocationData current = TravelData.getCurrentLocation();
        if (current == null) {
            Utilities.cancellableToast("Error loading current location. Please enable location.", this);
        } else {
            plotLocation(current);
        }
    }

    @Override
    protected void plotLocation(LocationData current) {
        LatLng mCurrentLocation = new LatLng(current.getLatitude(), current.getLongitude());

        if (!SessionData.isDriver(SessionManager.getRoleName(this))) {
            this.mLocationRequest.stopLocationUpdates(); //just mark the first location
        }

        if (!startedTrip || SessionData.isDriver(SessionManager.getRoleName(this))) {
            if (firstload) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f));
            } else {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocation));
            }
        }

        firstload = false;
        int color = R.color.grey_medium;
        if (startedTrip) {
            color = R.color.DodgerBlue;
        }

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(mCurrentLocation)
                .title("Current Location")
                .snippet("")
                .icon(createNormalMarkerView(color)));

        moveCurrentMarker(mCurrentLocation);
    }

    @Override
    public void onMapLoaded() {
        mGoogleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void onRecord(boolean start) {
        String msg = "Starting ...";
        if (!start) msg = "Stopping ...";
        showProgress(msg);
        RecordStartStopTask task = new RecordStartStopTask(start, this);
        task.execute();
    }

    public void onCancel(boolean start) {

    }

    public void toggleRecord() {
       // if (SessionData.isDriver(SessionManager.getRoleName(this))) {
            //MapUtils.showRecordButton(this, !startedTrip, this);
        //}

        boolean driver = SessionData.isDriver(SessionManager.getRoleName(this));
        String append = "Trip";
        if (!driver) append = "Track";

        String text;

        if (!startedTrip) {
            text = "  Start " + append;
        } else {
            text = "  End " + append;
        }

        Spannable buttonLabel = new SpannableString(text);
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.power,
                ImageSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        recordButton.setText(buttonLabel);
    }

    public void onStartedTrip(String trip) {
        startedTrip = true;
        toggleRecord();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGoogleMap.clear();
        currentMarker = null;
        dismissProgress();
    }

    public void onEndedTrip(String trip) {
        startedTrip = false;
        toggleRecord();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //have to show the statistics and then clear?
        mGoogleMap.clear();
        currentMarker = null;
        dismissProgress();
        showProgress("Retriving Trip details");
        TravelData.getTripSummary(trip);
    }

    public void onUploadLocations(String trip) {

    }

    @Override
    public void onLocationChanged(LocationData locationData) {
        LatLng mCurrentLocation = new LatLng(locationData.getLatitude(), locationData.getLongitude());

        startedTrip = true;
        toggleRecord();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (firstload) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f));
        } else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocation));
        }

        firstload = false;
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(mCurrentLocation)
                .title("Tracked Location")
                .snippet("")
                .icon(createTrackedMarkerView()));

    }

    public void onError(String msg) {
        dismissProgress();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onTripSummary(TripSummary summary) {
        dismissProgress();
        Logger.d(TAG, "Retrieved summary: " + summary);
        MapUtils.showSummary(this, summary, this);
    }

    public void onSave(String name, TripSummary summary) {

    }

    public void onCancel(TripSummary summary) {

    }

    public void doneRead(LocationData latest) {

        dismissProgress();
        if (latest != null) {
            LatLng mCurrentLocation = new LatLng(latest.getLatitude(), latest.getLongitude());
            moveCurrentMarker(mCurrentLocation);
        }

    }

    public void onTripEnded() {
        startedTrip = false;
        toggleRecord();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onError("Trip Has Ended.");
    }

    public class RecordStartStopTask extends AsyncTask<Void, Void, Void> {

        private boolean start;
        private TrackLocationActivity mParent;

        RecordStartStopTask(boolean ph, TrackLocationActivity activity) {
            start = ph;
            mParent = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (SessionData.isDriver(SessionManager.getRoleName(mParent))) {
                if (start) {
                    //TripSummary summary = new TripSummary();
                    //MapUtils.showSummary(this, summary, this);
                    //showProgress("Starting trip");
                    boolean started = TravelData.startTrip();
                    if (!started) {
                        dismissProgress();
                        Toast.makeText(mParent, "Location cannot be got. Need location to start trip.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //showProgress("Stopping trip");
                    boolean ended = TravelData.endTrip();
                    if (!ended) {
                        dismissProgress();
                        Toast.makeText(mParent, "Location cannot be got. Need location to end trip.", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                SessionData data = SessionData.fromStore(mParent);
                Set<String> numbers = data.getTrackNumbers();

                if (start) {
                    //showProgress("Checking for trip");
                    for (String num : numbers) {
                        TravelData.trackRoute(num, mParent);
                    }
                } else {
                    //showProgress("Stopping trip");
                    for (String num : numbers) {
                        TravelData.stopTrack(num);
                        startedTrip = false;
                    }
                    toggleRecord();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    dismissProgress();
                }
            }
            return null;
        }
    }

}
