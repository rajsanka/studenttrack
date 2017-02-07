package xchg.online.studenttrack;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;

import org.xchg.online.baseframe.activity.BaseActivity;
import org.xchg.online.baseframe.utils.AppPermissionConstants;
import org.xchg.online.baseframe.utils.Logger;
import org.xchg.online.baseframe.utils.Utilities;

import java.lang.ref.WeakReference;

import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.utils.MapUtils;
import xchg.online.studenttrack.utils.OnErrorDialogDismissedListener;
import xchg.online.studenttrack.utils.TrafficConstants;

import static xchg.online.studenttrack.utils.MapUtils.putDefaultCity;


/**
 * Created by rsankarx on 16/01/17.
 */

public abstract class BaseLocationActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, OnErrorDialogDismissedListener {

    private static final int MSG_LOCATION_TIME_OUT = 1091;
    private static final long LOCATION_HANDLER_TIME_OUT = 30000;
    private String TAG = BaseLocationActivity.class.getSimpleName();

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private static final int REQUEST_ID_GPS_SETTINGS = 3191;
    private static final int LOCATION_REQUEST_NO_OF_UPDATE = 1;

    private static class LocationTimeOutHandler extends Handler {
        private WeakReference<Activity> mActivity;
        private String TAG = LocationTimeOutHandler.class.getSimpleName();

        public LocationTimeOutHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
                Logger.d(TAG, "Time out in retrieving location.");
                //activity.putDefaultCity();
            }
        }
    }

    private JsonObjectRequest mRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationTimeOutHandler mLocationTimeOutHandler;
    private Toast mClickToast;
    private boolean mResolvingError;

    protected int noRequests = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()");
        setContentView(R.layout.activity_splash_screen);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(TrafficConstants.STATE_RESOLVING_ERROR, false);
        mClickToast = new Toast(this);
        buildGoogleApiClient();

    }

    protected abstract void handleLocationChange();

    protected void updateLocation(double lat, double lng) {
        Logger.d(TAG, "Adding next location.");
        TravelData.nextRouteLocation(lat, lng);
        handleLocationChange();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Logger.d(TAG, "connected to google client");
        Utilities.checkLocationPermission(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
            /*Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                updateLocation(location.getLatitude(), location.getLongitude());
            } else {
                updateLocation(0, 0);
                Utilities.showToast(this, "Unable to get location please make sure you are able to get your current location in google maps");
            }*/
        } else {
            updateLocation(0, 0);
            Utilities.showToast(this, "Location access is required to continue further.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logger.d(TAG, "connectionResult" + connectionResult.toString());
        if (!mResolvingError) {
            mResolvingError = true;
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, TrafficConstants.REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    mGoogleApiClient.connect();
                }
            } else {
                if (!this.isFinishing()) {
                    MapUtils.showErrorDialog(this, getSupportFragmentManager(), connectionResult.getErrorCode());
                }
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    //.enableAutoManage(SplashScreenActivity.this, 0, this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        createLocationRequest();
    }

    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
            if (noRequests > 0)
                mLocationRequest.setNumUpdates(noRequests);
        }
    }

    protected void startLocationUpdates() {
        Logger.d(TAG, "startLocationUpdates: start Location Updates." + mGoogleApiClient.isConnected() + ":" + this.getClass().getName());

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Logger.d(TAG, "startLocationUpdates: Cannot find the permissions to access locations");
                return;
            }
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            mLocationTimeOutHandler = new LocationTimeOutHandler(this);
            mLocationTimeOutHandler.sendEmptyMessageDelayed(MSG_LOCATION_TIME_OUT, LOCATION_HANDLER_TIME_OUT);
            Logger.d(TAG, "location update started");
            pendingResult.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    Logger.d(TAG, "onResult(" + status.isSuccess() + ")");
                    if (!status.isSuccess()) {
                        Logger.d(TAG, "not able to get current location");
                        String msg = getString(R.string.location_not_found_msg);
                        MapUtils.cancellableToast(BaseLocationActivity.this, mClickToast, msg);
                        MapUtils.putDefaultCity();
                    }
                }
            });
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.i(TAG, "onLocationChanged called " + this.getClass().getName());
        if (mLocationTimeOutHandler != null) {
            mLocationTimeOutHandler.removeCallbacksAndMessages(null);
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        updateLocation(latitude, longitude);
    }

    @Override
    public void onDialogDismissed() {
        mResolvingError = false;
        finish();
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        int statusCode = locationSettingsResult.getStatus().getStatusCode();
        Logger.d(TAG, "onResult locationSettingsResult status code = " + statusCode);
        switch (statusCode) {
            case LocationSettingsStatusCodes.SUCCESS:
                Utilities.checkLocationPermission(this);
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    locationSettingsResult.getStatus().startResolutionForResult(this, REQUEST_ID_GPS_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    MapUtils.putDefaultCity();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                MapUtils.putDefaultCity();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");
        switch (requestCode) {
            case REQUEST_ID_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // connect google client before staring location update.
                        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                            mGoogleApiClient.connect();
                        }
                        Utilities.checkLocationPermission(this);
                        break;
                    case Activity.RESULT_CANCELED:
                        String msg = getString(R.string.location_not_found_msg);
                        MapUtils.cancellableToast(this, mClickToast, msg);
                        putDefaultCity();
                        break;
                }
                break;
            case TrafficConstants.REQUEST_RESOLVE_ERROR:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mResolvingError = false;
                        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting())
                            mGoogleApiClient.connect();
                        break;
                    case Activity.RESULT_CANCELED:
                        mResolvingError = true;
                        finish();
                        break;
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == AppPermissionConstants.PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                String msg = getString(R.string.splash_city_not_found_msg);
                MapUtils.cancellableToast(this, mClickToast, msg);
                MapUtils.putDefaultCity();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TrafficConstants.STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d(TAG, "onStart");
        if (!mResolvingError) {
            Logger.d(TAG, "connection Client");
            mGoogleApiClient.connect();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop() " + this.getClass().getName());
        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy()");
        if (mRequest != null) {
            mRequest.cancel();
        }
        if (mLocationTimeOutHandler != null) {
            mLocationTimeOutHandler.removeCallbacksAndMessages(null);
        }
        mRequest = null;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
        super.onDestroy();
    }
}
