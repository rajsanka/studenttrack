package xchg.online.studenttrack.smart.trackflow;

import android.app.Activity;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;
import org.xchg.online.baseframe.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.data.TripSummary;

/**
 * Created by rsankarx on 16/01/17.
 */

public class GetTripSummary extends SmartEvent {

    private static final String TAG = GetTripSummary.class.getSimpleName();


    public class GetTripSummaryListener implements SmartResponseListener {

        GetTripSummaryListener() {
        }

        public void handleResponse(List responses){
            Logger.i(TAG, "StartTrip: " + responses);
            Map val = (Map)responses.get(0);
            TripSummary summary = TravelData.addTripSummary(val);
            TravelData.getListener().onTripSummary(summary);
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

    private static final String FLOW = "TrackFlow";

    private String tripName;

    public GetTripSummary(String tn) {
        super(FLOW);
        Logger.d(TAG, "GetTripSummary: getting summary for trip");
        tripName = tn;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        return ret;
    }

    public void postTo(Activity activity) {
        Logger.d(TAG, "Posting GetTripSummary to server: " + tripName);
        super.postEvent(activity, new GetTripSummaryListener(), "Trip", tripName);
    }
}
