package xchg.online.register;

import android.app.Activity;
import android.util.Log;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 12/12/16.
 */

public class LookupEvent extends SmartEvent {

    public interface LookupProfileListener {
        public void onProfile(Map data);
        public void onError(String msg);
    }

    public class LookupSmartListener implements SmartResponseListener {

        LookupProfileListener listener;

        LookupSmartListener(LookupProfileListener l) {
            listener = l;
        }

        @Override
        public void handleResponse(List list) {
            List result = (List) ((Map)list.get(0)).get("result");
            if ((result != null) && (result.size() > 0)) {
                listener.onProfile((Map)result.get(0));
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
    private static final String FLOW = "ProfileFlow";
    private String email;

    public LookupEvent(String e) {
        super(FLOW);
        email = e;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();
        parms.put("group", "Profile");
        parms.put("key", email);
        return parms;
    }

    public void postTo(Activity activity, LookupProfileListener listener) {
        super.postEvent(activity, new LookupSmartListener(listener), "FlowAdmin", FLOW);
    }
}
