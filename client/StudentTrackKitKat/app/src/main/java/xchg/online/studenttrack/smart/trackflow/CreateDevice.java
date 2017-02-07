package xchg.online.studenttrack.smart.trackflow;

import android.app.Activity;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rsankarx on 16/01/17.
 */

public class CreateDevice extends SmartEvent {
    private String deviceId;
    private String phoneNumber;
    private boolean isRoaming;
    private String country;

    private static final String FLOW = "TrackFlow";

    public CreateDevice() {
        super(FLOW);
    }

    public void setDeviceId(String d) { deviceId = d; }
    public void setPhoneNumber(String p) { phoneNumber = p; }
    public void setRoaming(boolean b) { isRoaming = b; }
    public void setCountry(String c) { country = c; }

    public Map<String, Object> getParams() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("deviceId", deviceId);
        ret.put("phoneNumber", phoneNumber);
        ret.put("isRoaming", isRoaming);
        ret.put("country", country);
        return ret;
    }

    public void postTo(Activity activity, SmartResponseListener listener) {
        super.postEvent(activity, listener, "FlowAdmin", FLOW);
    }
}
