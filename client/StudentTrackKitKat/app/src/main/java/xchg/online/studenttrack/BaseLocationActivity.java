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
import org.xchg.online.baseframe.utils.SessionManager;
import org.xchg.online.baseframe.utils.Utilities;

import java.lang.ref.WeakReference;
import java.util.List;

import xchg.online.studenttrack.data.LocationData;
import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.utils.MapUtils;
import xchg.online.studenttrack.utils.OnErrorDialogDismissedListener;
import xchg.online.studenttrack.utils.TrafficConstants;

import static xchg.online.studenttrack.utils.MapUtils.putDefaultCity;


/**
 * Created by rsankarx on 16/01/17.
 */

public abstract class BaseLocationActivity extends BaseActivity implements OnErrorDialogDismissedListener, BaseLocationUpdates.LocationParent {

    private String TAG = BaseLocationActivity.class.getSimpleName();

    private static final int REQUEST_ID_GPS_SETTINGS = 3191;

    protected BaseLocationUpdates mLocationRequest;
    private Toast mClickToast;
    private boolean mResolvingError;

    protected int noRequests = -1;
    private static Intent mService;
    protected static boolean inBackground;
    protected static boolean starting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()");
        setContentView(R.layout.activity_splash_screen);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(TrafficConstants.STATE_RESOLVING_ERROR, false);
        mClickToast = new Toast(this);
        mLocationRequest = BaseLocationUpdates.createLocationUpdates(this, this);
        mLocationRequest.buildGoogleApiClient();
        if (!inBackground) {
            inBackground = false;
            starting = true;
        }
    }

    protected abstract void handleLocationChange();
    protected abstract void plotLocation(LocationData current);

    @Override
    public void onNoLocationPermission() {
        Utilities.checkLocationPermission(this);
    }

    @Override
    public void onCannotGetCurrentLocation() {
        mLocationRequest.updateLocation(0, 0);
        String msg = getString(R.string.location_not_found_msg);
        MapUtils.cancellableToast(BaseLocationActivity.this, mClickToast, msg);
        MapUtils.putDefaultCity();
    }

    @Override
    public void onResolutionRequired(LocationSettingsResult locationSettingsResult) {
        try {
            locationSettingsResult.getStatus().startResolutionForResult(this, REQUEST_ID_GPS_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            MapUtils.putDefaultCity();
        }
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
                    mLocationRequest.reconnect();
                }
            } else {
                if (!this.isFinishing()) {
                    MapUtils.showErrorDialog(this, getSupportFragmentManager(), connectionResult.getErrorCode());
                }
            }
        }
    }

    @Override
    public void handleLocationChange(double lat, double lng) {
        handleLocationChange();
    }


    @Override
    public void onDialogDismissed() {
        mResolvingError = false;
        finish();
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
                        mLocationRequest.reconnect();
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
                        mLocationRequest.reconnect();
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
                mLocationRequest.startLocationUpdates();
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
            mLocationRequest.reconnect();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop() " + this.getClass().getName());
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy()");
        mLocationRequest.destroy();
        super.onDestroy();
    }

    public int getNoRequests() {
        return noRequests;
    }

    public synchronized void goBackground() {
        if (!inBackground) {
            inBackground = true;
            mLocationRequest.stopLocationUpdates();
            mService = new Intent(this, LocationService.class);
            startService(mService);
        }
    }

    public synchronized void goForeground() {
        if (inBackground) {
            inBackground = false;
            stopService(mService);

            mLocationRequest.startLocationUpdates();
        }
    }

    public synchronized void toggleForeBackground(boolean fore) {
        if (SessionManager.isLoggedIn(this)) {
            if (fore) {
                goForeground();
            } else {
                goBackground();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (starting) {
            starting = false;
        } else {
            Logger.i(TAG, "onResume of BaseLocationActivity");
            toggleForeBackground(true);
        }
    }

    public void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause of BaseLocationActivity");
        toggleForeBackground(false);
    }

}
