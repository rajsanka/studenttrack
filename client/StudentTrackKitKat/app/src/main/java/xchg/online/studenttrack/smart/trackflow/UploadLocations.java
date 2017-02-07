package xchg.online.studenttrack.smart.trackflow;

import android.app.Activity;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;
import org.xchg.online.baseframe.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xchg.online.studenttrack.data.LocationData;
import xchg.online.studenttrack.data.TravelData;

/**
 * Created by rsankarx on 16/01/17.
 */

public class UploadLocations extends SmartEvent {

    static class VehicleLocation {
        double latitude;
        double longitude;
        long startTime;
        /*double direction;
        double altitude;
        double accuracy;
        double distance;
        double speed;
        long duration;*/
    }
    private static final String TAG = UploadLocations.class.getSimpleName();
    private static final String FLOW = "TrackFlow";

    public class UploadLocationsListener implements SmartResponseListener {
        public void handleResponse(List responses){
            Logger.i(TAG, "Uploaded Locations: " + responses);
            TravelData.getListener().onUploadLocations(routeName);
        }

        public void handleError(double code, String context){
            Logger.i(TAG, "Error Uploading Locations: " + code + ":" + context);
            TravelData.getListener().onError(code + ":" + context);
        }

        public void handleNetworkError(String message){
            Logger.i(TAG, "Error Uploading Locations: " + message);
            TravelData.getListener().onError(message);
        }
    }

    private String deviceId;
    private String routeName;
    private List<VehicleLocation> locations;

    private List<VehicleLocation> posted;


    public UploadLocations(String devId, String nm) {
        super(FLOW);
        deviceId = devId;
        routeName = nm;
        locations = new ArrayList<VehicleLocation>();
    }

    public void addLocation(LocationData data) {
        synchronized (locations) {
            Logger.d(UploadLocations.class.getSimpleName(), "UploadLocation: Adding next location tp upload: " + data);
            VehicleLocation loc = new VehicleLocation();
            loc.latitude = data.getLatitude();
            loc.longitude = data.getLongitude();
            loc.startTime = data.getStartTime();
            /*loc.distance = data.getDistance();
            loc.speed = data.getSpeed();
            loc.duration = data.getDuration();*/
            locations.add(loc);
        }
    }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("deviceId", deviceId);

        posted = new ArrayList<VehicleLocation>();

        synchronized (locations) {
            posted.addAll(locations);
            locations.clear();
        }
        ret.put("locations", posted);
        Logger.d(UploadLocations.class.getSimpleName(), "Posting: " + ret);

        return ret;
    }

    public void postTo(Activity activity) {
        Logger.d(UploadLocations.class.getSimpleName(), "Posting Locations to server: " + locations.size());
        if (locations.size() > 0) super.postEvent(activity, new UploadLocationsListener(), "Trip", routeName);
    }
}
