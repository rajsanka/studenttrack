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

public class VerifyDriver extends SmartEvent {

    public interface VerifiedDriver {
        public void onSuccess();
        public void onError(String msg);
    }

    public class VerifySmartListener implements SmartResponseListener {

        VerifiedDriver listener;

        VerifySmartListener(VerifiedDriver l) {
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
    private String OTP;
    private String password;
    private String phone;

    public VerifyDriver(String ph, String otp, String pwd) {
        super(FLOW);
        OTP = otp;
        phone = ph;
        password = pwd;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();
        parms.put("oneTimePassword", OTP);
        parms.put("password", password);
        return parms;
    }

    public void postTo(Activity activity, VerifiedDriver listener) {
        super.postEvent(activity, new VerifySmartListener(listener), "Driver", phone);
    }
}
