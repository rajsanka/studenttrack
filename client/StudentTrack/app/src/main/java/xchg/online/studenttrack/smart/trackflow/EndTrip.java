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

public class EndTrip extends SmartEvent {
    public class EndTripListener implements SmartResponseListener {
        EndTripListener() {
        }

        public void handleResponse(List responses){
            Logger.i(TAG, "EndTrip: " + responses);
            Map val = (Map)responses.get(0);
            TravelData.endRoute(tripName, endAddress, endLocation.latitude, endLocation.longitude);
            TravelData.getListener().onEndedTrip(tripName);
            TravelData.startUnTracked(endLocation.latitude, endLocation.longitude);
        }

        public void handleError(double code, String context){
            Logger.i(TAG, "Error EndTrip: " + code + ":" + context);
            TravelData.getListener().onError(code + ":" + context);
        }

        public void handleNetworkError(String message){
            Logger.i(TAG, "Network Error EndTrip: " + message);
            TravelData.getListener().onError(message);
        }
    }

    private String tripName;
    private String endAddress;
    private UploadLocations.VehicleLocation endLocation;

    private static final String TAG = StartTrip.class.getSimpleName();
    private static final String FLOW = "TrackFlow";

    public EndTrip(String trip, LocationData end, String addr) {
        super(FLOW);
        Logger.d(TAG, "StartTrip: Setting up start Location: " + end);
        endLocation = new UploadLocations.VehicleLocation();
        endLocation.latitude = end.getLatitude();
        endLocation.longitude = end.getLongitude();
        endLocation.startTime = end.getStartTime();
        endAddress = addr;
        tripName = trip;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("endLocation", endLocation);
        ret.put("endAddress", endAddress);

        return ret;
    }

    public void postTo(Activity activity) {
        Logger.d(TAG, "Posting Locations to server: ");
        super.postEvent(activity, new EndTripListener(), "Trip", tripName);
    }
}
