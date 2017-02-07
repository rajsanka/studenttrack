package xchg.online.studenttrack.smart.trackflow;

import android.app.Activity;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;
import org.xchg.online.baseframe.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xchg.online.studenttrack.data.LocationData;
import xchg.online.studenttrack.data.TravelData;

/**
 * Created by rsankarx on 16/01/17.
 */

public class StartTrip extends SmartEvent {

    public class StartTripListener implements SmartResponseListener {
        StartTripListener() {
        }

        public void handleResponse(List responses){
            Logger.i(TAG, "StartTrip: " + responses);
            Map val = (Map)responses.get(0);
            String trip = (String) val.get("tripName");
            TravelData.startRoute(trip, startAddress, startLocation.latitude, startLocation.longitude);
            TravelData.getListener().onStartedTrip(trip);
        }

        public void handleError(double code, String context){
            Logger.i(TAG, "Error StartTrip: " + code + ":" + context);
            TravelData.getListener().onError(code + ":" + context);
        }

        public void handleNetworkError(String message){
            Logger.i(TAG, "Network Error StartTrip: " + message);
            TravelData.getListener().onError(message);
        }
    }

    private String deviceId;
    private String startAddress;
    private UploadLocations.VehicleLocation startLocation;

    private static final String TAG = StartTrip.class.getSimpleName();
    private static final String FLOW = "TrackFlow";

    public StartTrip(String devId, LocationData start, String addr) {
        super(FLOW);
        Logger.d(TAG, "StartTrip: Setting up start Location: " + start + ":" + devId);
        startLocation = new UploadLocations.VehicleLocation();
        startLocation.latitude = start.getLatitude();
        startLocation.longitude = start.getLongitude();
        startLocation.startTime = start.getStartTime();
        startAddress = addr;
        deviceId = devId;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("startLocation", startLocation);
        ret.put("startAddress", startAddress);

        return ret;
    }

    public void postTo(Activity activity) {
        Logger.d(TAG, "Posting Locations to server: " + deviceId);
        super.postEvent(activity, new StartTripListener(), "Device", deviceId);
    }
}
