package org.anon.smart.client;

import android.app.Activity;

import java.util.Map;

/**
 * Created by rsankarx on 24/10/16.
 */

public abstract class SmartEvent {

    public static void setSmartServer(Activity activity, String ip, int p, String tenant) {
        SmartCommunicator.getInstance(activity).setSmartServer(ip, p, tenant);
    }

    protected abstract Map<String, Object> getParams();

    private String flow;
    private String event;

    protected SmartEvent() {
        event = this.getClass().getSimpleName();

        //assumption is that the event is present under the flow package.
        String pkg = this.getClass().getName();
        String[] names = pkg.split("\\.");
        if (names.length >= 2) {
            flow = names[names.length - 2];
        } else {
            flow = "NoDetect";
        }
    }

    protected SmartEvent(String f) {
        event = this.getClass().getSimpleName();
        flow = f;
    }

    public void postEvent(Activity activity, SmartResponseListener listener, String postTo, Object val) {
        SmartCommunicator.getInstance(activity).queueSmartRequest(flow, event,
                postTo, val, getParams(), listener);
    }
}
