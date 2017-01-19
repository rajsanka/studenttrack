package xchg.online.studenttrack.smart.trackflow;

/**
 * Created by rsankarx on 16/01/17.
 */

import android.app.Activity;
import android.util.Log;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by rsankarx on 16/01/17.
 */

public class LookupEvent extends SmartEvent {

    public interface LookupDataListener {
        public void onData(Map data);
        public void noData();
        public void onError(String msg);
    }

    public class LookupSmartListener implements SmartResponseListener {

        LookupDataListener listener;

        LookupSmartListener(LookupDataListener l) {
            listener = l;
        }

        @Override
        public void handleResponse(List list) {
            List result = (List) ((Map)list.get(0)).get("result");
            if ((result != null) && (result.size() > 0)) {
                listener.onData((Map)result.get(0));
            } else {
                listener.noData();
            }
        }

        @Override
        public void handleError(double code, String context) {
            String message = code + ":" + context;
            Log.i(TAG, "Error Searching data: " + message);
            listener.onError(message);
        }

        @Override
        public void handleNetworkError(String message) {
            Log.i(TAG, "Error Searching data: " + message);
            listener.onError(message);
        }
    }

    private static final String TAG = LookupEvent.class.getSimpleName();
    private static final String FLOW = "StudentFlow";
    private String phone;
    private String object;

    public LookupEvent(String obj, String ph) {
        super(FLOW);
        phone = ph;
        object = obj;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();
        parms.put("group", object);
        parms.put("key", phone);
        return parms;
    }

    public void postTo(Activity activity, LookupDataListener listener) {
        super.postEvent(activity, new LookupSmartListener(listener), "FlowAdmin", FLOW);
    }
}

