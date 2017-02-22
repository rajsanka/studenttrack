package xchg.online.studenttrack;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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

import org.xchg.online.baseframe.utils.Logger;
import org.xchg.online.baseframe.utils.Utilities;

import java.lang.ref.WeakReference;

import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.utils.MapUtils;


/**
 * Created by rsankarx on 07/02/17.
 */

public class BaseLocationUpdates implements GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    public interface LocationParent {
        public void onNoLocationPermission();
        public void onConnectionFailed(ConnectionResult connectionResult);
        public void onResolutionRequired(LocationSettingsResult locationSettingsResult);
        public int getNoRequests();
        public void onCannotGetCurrentLocation();
        public void handleLocationChange(double lat, double lng);
    }

    private static class LocationTimeOutHandler extends Handler {
        private WeakReference<Context> mActivity;
        private String TAG = LocationTimeOutHandler.class.getSimpleName();

        public LocationTimeOutHandler(Context activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Context activity = mActivity.get();
            if (activity != null) {
                Logger.d(TAG, "Time out in retrieving location.");
            }
        }
    }

    private static final String TAG = BaseLocationUpdates.class.getSimpleName();

    private static final int MSG_LOCATION_TIME_OUT = 1091;
    private static final long LOCATION_HANDLER_TIME_OUT = 30000;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private static final int REQUEST_ID_GPS_SETTINGS = 3191;

    private JsonObjectRequest mRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private LocationParent mParent;
    private int noRequests = -1;
    private LocationTimeOutHandler mLocationTimeOutHandler;

    public static BaseLocationUpdates createLocationUpdates(Context ctx, LocationParent parent) {
        return new BaseLocationUpdates(ctx, parent);
    }

    private BaseLocationUpdates(Context ctx, LocationParent parent) {
        mContext = ctx;
        mParent = parent;
        noRequests = parent.getNoRequests();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.d(TAG, "connected to google client");

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            mParent.onNoLocationPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void reconnect() {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d(TAG, "connectionResult" + connectionResult.toString());
        mParent.onConnectionFailed(connectionResult);
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
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
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Logger.d(TAG, "startLocationUpdates: Cannot find the permissions to access locations");
                return;
            }
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            mLocationTimeOutHandler = new LocationTimeOutHandler(mContext);
            mLocationTimeOutHandler.sendEmptyMessageDelayed(MSG_LOCATION_TIME_OUT, LOCATION_HANDLER_TIME_OUT);
            Logger.d(TAG, "location update started");
            pendingResult.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    Logger.d(TAG, "onResult(" + status.isSuccess() + ")");
                    if (!status.isSuccess()) {
                        Logger.d(TAG, "not able to get current location");
                        mParent.onCannotGetCurrentLocation();
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
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        int statusCode = locationSettingsResult.getStatus().getStatusCode();
        Logger.d(TAG, "onResult locationSettingsResult status code = " + statusCode);
        switch (statusCode) {
            case LocationSettingsStatusCodes.SUCCESS:
                mParent.onNoLocationPermission();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                mParent.onResolutionRequired(locationSettingsResult);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                MapUtils.putDefaultCity();
                break;
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

    protected void updateLocation(double lat, double lng) {
        Logger.d(TAG, "Adding next location.");
        TravelData.nextRouteLocation(lat, lng);
        mParent.handleLocationChange(lat, lng);
    }

    public void destroy() {
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
    }
}
