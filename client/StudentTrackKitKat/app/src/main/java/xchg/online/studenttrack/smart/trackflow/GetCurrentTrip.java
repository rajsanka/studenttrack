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

public class GetCurrentTrip extends SmartEvent {

    public static class OnGetCurrentTripListener implements SmartResponseListener {

        GetCurrentTripLocation.TripLocationListener listener;

        OnGetCurrentTripListener(GetCurrentTripLocation.TripLocationListener l) {
            listener = l;
        }

        @Override
        public void handleResponse(List list) {
            Map map = (Map)list.get(0);
            Object val = map.get("message");
            LocationData latest = null;

            if (val != null) {
                listener.onNoTrip(val.toString());
            } else {
                String nm = map.get("tripName").toString();
                listener.onTripName(nm);
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
            listener.onError(s);
        }

        @Override
        public void handleNetworkError(String s) {
            listener.onError(s);
        }
    }

    private static final String FLOW = "TrackFlow";
    private static final String TAG = GetCurrentTrip.class.getSimpleName();

    private String phone;

    public GetCurrentTrip(String ph) {
        super(FLOW);
        phone = ph;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();

        return parms;
    }

    public void postTo(Activity activity, GetCurrentTripLocation.TripLocationListener listener) {
        super.postEvent(activity, new OnGetCurrentTripListener(listener), "Device", phone);
    }
}
