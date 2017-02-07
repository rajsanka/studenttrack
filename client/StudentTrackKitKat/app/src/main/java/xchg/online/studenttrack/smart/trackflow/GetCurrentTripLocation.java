package xchg.online.studenttrack.smart.trackflow;

import android.app.Activity;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;
import org.xchg.online.baseframe.utils.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xchg.online.studenttrack.data.LocationData;

/**
 * Created by rsankarx on 17/01/17.
 */

public class GetCurrentTripLocation extends SmartEvent {

    public static interface TripLocationListener {
        public void onTripName(String nm);
        public void onLocation(LocationData locationDatas);
        public void onError(String msg);
        public void onNoTrip(String msg);
        public void tripEnded(String msg);
        public void doneRead(LocationData latest);
    }

    public static class OnGetCurrentTripLocationListener implements SmartResponseListener {

        TripLocationListener listener;

        OnGetCurrentTripLocationListener(TripLocationListener l) {
            listener = l;
        }

        @Override
        public void handleResponse(List list) {
            Map map = (Map)list.get(0);
            /*Double lng = (Double) map.get("lastLongitude");
            Double lat = (Double) map.get("lastLatitude");
            LocationData loc = LocationData.getLocation(lat, lng, null);
            listener.onLocation(loc);*/
            LocationData latest = null;
            Object val = map.get("message");
            if (val != null) {
                listener.tripEnded(val.toString());
            } else {
                List route = (List) map.get("route");
                LocationData prev = null;
                for (int i = 0; i < route.size(); i++) {
                    Map m = (Map) route.get(i);
                    LocationData data = LocationData.getLocation((Double) m.get("latitude"), (Double) m.get("longitude"), Utilities.getLong(m.get("startTime")), prev);
                    if (latest == null) {
                        latest = data;
                    } else if (data.getStartTime() > latest.getStartTime()) {
                        latest = data;
                    }
                    listener.onLocation(data);
                }
            }

            listener.doneRead(latest);
        }

        @Override
        public void handleError(double v, String s) {
            listener.onError(v + ":" + s);
        }

        @Override
        public void handleNetworkError(String s) {
            listener.onError(s);
        }
    }

    private static final String FLOW = "TrackFlow";
    private static final String TAG = GetCurrentTripLocation.class.getSimpleName();

    private String phone;
    private long lastTime;

    public GetCurrentTripLocation(String ph, long last) {
        super(FLOW);
        phone = ph;
        lastTime = last;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();
        parms.put("lastTime", lastTime);
        return parms;
    }

    public void postTo(Activity activity, TripLocationListener listener) {
        super.postEvent(activity, new OnGetCurrentTripLocationListener(listener), "Device", phone);
    }

}
