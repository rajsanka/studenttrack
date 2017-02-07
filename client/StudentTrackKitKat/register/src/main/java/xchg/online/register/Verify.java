package xchg.online.register;

import android.app.Activity;
import android.util.Log;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 18/11/16.
 */

public class Verify extends SmartEvent {
    private static final String TAG = Verify.class.getSimpleName();

    public static interface VerifyListener {
        public void onSuccess();
        public void onError(String msg);
    }

    class VerifySmartListener implements SmartResponseListener {

        VerifyListener listener;

        VerifySmartListener(VerifyListener l) {
            listener = l;
        }

        public void handleResponse(List responses){
            Log.i(TAG, "Verify: " + responses);
            listener.onSuccess();
        }

        public void handleError(double code, String context){
            Log.i(TAG, "Error Verify: " + code + ":" + context);
            listener.onError(code + ":" + context);
        }

        public void handleNetworkError(String message){
            Log.i(TAG, "Error Verify: " + message);
            listener.onError(message);
        }
    }

    private String code;
    private String email;
    private String password;

    private static final String FLOW = "ProfileFlow";

    public Verify(String e) {
        super(FLOW);
        email = e;
    }

    public void setCode(String e) { code = e; }
    public void setPassword(String n) { password = n; }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("code", code);
        ret.put("password", password);

        return ret;
    }

    public void postTo(Activity activity, VerifyListener listener) {
        super.postEvent(activity, new VerifySmartListener(listener), "Profile", email);
    }
}
