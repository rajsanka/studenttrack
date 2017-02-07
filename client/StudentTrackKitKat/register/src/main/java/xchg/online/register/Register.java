package xchg.online.register;

import android.app.Activity;
import android.util.Log;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 18/11/16.
 */

public class Register extends SmartEvent {
    private static final String TAG = Register.class.getSimpleName();

    public static interface RegisterListener {
        public void onSuccess(String email);
        public void onError(String msg);
    }

    class RegisterSmartListener implements SmartResponseListener {

        RegisterListener listener;

        RegisterSmartListener(RegisterListener l) {
            listener = l;
        }

        public void handleResponse(List responses){
            Log.i(TAG, "Uploaded Locations: " + responses);
            listener.onSuccess(email);
        }

        public void handleError(double code, String context){
            Log.i(TAG, "Error Uploading Locations: " + code + ":" + context);
            listener.onError(code + ":" + context);
        }

        public void handleNetworkError(String message){
            Log.i(TAG, "Error Uploading Locations: " + message);
            listener.onError(message);
        }
    }

    private String email;
    private String name;
    private String phone;
    private String defaultCity;
    private List<String> roles;

    private static final String FLOW = "ProfileFlow";

    public Register(String role) {
        super(FLOW);
        roles = new ArrayList<>();
        roles.add(role); //registers for userole
    }

    public void setEmail(String e) { email = e; }
    public void setName(String n) { name = n; }
    public void setPhone(String p) { phone = p; }
    public void setDefaultCity(String c) { defaultCity = c; }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("email", email);
        ret.put("phone", phone);
        ret.put("name", name);
        ret.put("defaultCity", defaultCity);
        ret.put("roles", roles);
        return ret;
    }

    public void postTo(Activity activity, RegisterListener listener) {
        super.postEvent(activity, new RegisterSmartListener(listener), "FlowAdmin", FLOW);
    }
}
