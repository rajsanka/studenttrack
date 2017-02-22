package xchg.online.studenttrack;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationSettingsResult;

import xchg.online.studenttrack.data.LocationData;
import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.utils.MapUtils;
import xchg.online.studenttrack.utils.TrafficConstants;

public class LocationService extends Service implements BaseLocationUpdates.LocationParent {

    private static final String TAG = LocationService.class.getSimpleName();

    private BaseLocationUpdates mLocationUpdates;
    private boolean mResolvingError;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        mLocationUpdates = BaseLocationUpdates.createLocationUpdates(this, this);
        mLocationUpdates.buildGoogleApiClient();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        mLocationUpdates.reconnect();
        return START_STICKY;
    }


    @Override
    public void onNoLocationPermission() {
        Log.e(TAG, "No Location permission. Please grant this to run location service.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mResolvingError) {
            mResolvingError = true;

            Log.e(TAG, "Error in connection: " + connectionResult.getErrorCode());

        }
    }

    @Override
    public void onResolutionRequired(LocationSettingsResult locationSettingsResult) {
        MapUtils.putDefaultCity();
    }

    @Override
    public int getNoRequests() {
        return -1;
    }

    @Override
    public void onCannotGetCurrentLocation() {
        Log.i(TAG, "Cannot get current location");
        mLocationUpdates.updateLocation(0, 0);
    }

    @Override
    public void handleLocationChange(double lat, double lng) {
        Log.i(TAG, "handleLocationChange: " + lat + ":" + lng);
        LocationData current = TravelData.getCurrentLocation();
        current.setUnplotted(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy - onDestroy ");
        mLocationUpdates.destroy();
    }

}
