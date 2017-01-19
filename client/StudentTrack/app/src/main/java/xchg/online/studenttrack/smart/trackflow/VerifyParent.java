package xchg.online.studenttrack.smart.trackflow;

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

public class VerifyParent extends SmartEvent {
    public interface VerifiedParent {
        public void onSuccess();
        public void onError(String msg);
    }

    public class VerifySmartListener implements SmartResponseListener {

        VerifiedParent listener;

        VerifySmartListener(VerifiedParent l) {
            listener = l;
        }

        @Override
        public void handleResponse(List list) {
            listener.onSuccess();
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
    private String name;
    private String OTP;
    private String password;
    private String phone;

    public VerifyParent(String ph, String nm, String otp, String pwd) {
        super(FLOW);
        OTP = otp;
        phone = ph;
        password = pwd;
        name = nm;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();
        parms.put("oneTimePassword", OTP);
        parms.put("password", password);
        parms.put("phone", phone);
        parms.put("name", name);

        return parms;
    }

    public void postTo(Activity activity, VerifiedParent listener) {
        super.postEvent(activity, new VerifySmartListener(listener), "FlowAdmin", FLOW);
    }
}
