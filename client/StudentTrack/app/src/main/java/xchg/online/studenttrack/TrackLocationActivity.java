package xchg.online.studenttrack;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    private boolean startedTrip = false;


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

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        LocationData current = TravelData.getCurrentLocation();
        if (current == null) {
            Utilities.cancellableToast("Error loading current location. Please enable location.", this);
        } else {
            LatLng mCurrentLocation = new LatLng(current.getLatitude(), current.getLongitude());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f));

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .title("Current Location")
                    .snippet("")
                    .icon(createNormalMarkerView()));
        }
        mGoogleMap.setOnMapLoadedCallback(this);
        MapUtils.showRecordButton(this, true, this);

        //mGoogleMap.setOnInfoWindowClickListener(this);
    }

    private BitmapDescriptor createNormalMarkerView() {
        View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.location_marker, null);
        ImageView customerIcon = (ImageView) markerView.findViewById(R.id.customer_icon_iv);
        customerIcon.setImageResource(R.drawable.circle_shape);
        return BitmapDescriptorFactory.fromBitmap(Utilities.createBitmapFromView(this, markerView));
    }

    private BitmapDescriptor createTrackedMarkerView() {
        View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.location_marker, null);
        ImageView customerIcon = (ImageView) markerView.findViewById(R.id.customer_icon_iv);
        customerIcon.setImageResource(R.drawable.tracked_circle);
        return BitmapDescriptorFactory.fromBitmap(Utilities.createBitmapFromView(this, markerView));
    }

    protected void handleLocationChange() {
        LocationData current = TravelData.getCurrentLocation();
        if (current == null) {
            Utilities.cancellableToast("Error loading current location. Please enable location.", this);
        } else {
            LatLng mCurrentLocation = new LatLng(current.getLatitude(), current.getLongitude());

            if (!SessionData.isDriver(SessionManager.getRoleName(this))) {
                this.stopLocationUpdates(); //just mark the first location
            }

            if (!startedTrip || SessionData.isDriver(SessionManager.getRoleName(this))) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f));
            }
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .title("Current Location")
                    .snippet("")
                    .icon(createNormalMarkerView()));
        }


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

        if (SessionData.isDriver(SessionManager.getRoleName(this))) {
            if (start) {
                //TripSummary summary = new TripSummary();
                //MapUtils.showSummary(this, summary, this);
                showProgress("Starting trip");
                TravelData.startTrip();
            } else {
                showProgress("Stopping trip");
                TravelData.endTrip();
            }
        } else {
            SessionData data = SessionData.fromStore(this);
            Set<String> numbers = data.getTrackNumbers();

            if (start) {
                showProgress("Checking for trip");
                for (String num : numbers) {
                    TravelData.trackRoute(num, this);
                }
            } else {
                showProgress("Stopping trip");
                for (String num : numbers) {
                    TravelData.stopTrack(num);
                    startedTrip = false;
                }

                dismissProgress();
            }
        }
    }

    public void onCancel(boolean start) {

    }

    public void toggleRecord() {
       // if (SessionData.isDriver(SessionManager.getRoleName(this))) {
            MapUtils.showRecordButton(this, !startedTrip, this);
        //}
    }

    public void onStartedTrip(String trip) {
        startedTrip = true;
        mGoogleMap.clear();
        dismissProgress();
    }

    public void onEndedTrip(String trip) {
        startedTrip = false;

        //have to show the statistics and then clear?
        mGoogleMap.clear();
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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f));
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

    public void doneRead() {
        dismissProgress();
    }

}
