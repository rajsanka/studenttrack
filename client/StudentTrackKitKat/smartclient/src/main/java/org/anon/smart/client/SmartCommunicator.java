package org.anon.smart.client;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by rsankarx on 12/09/16.
 */
public class SmartCommunicator {
    private static SmartCommunicator ourInstance;

    public static synchronized SmartCommunicator getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new SmartCommunicator(context);
        }
        return ourInstance;
    }

    private RequestQueue _requestQueue;
    private Context _context;

    private String _server;
    private int _port;
    private String _tenant;
    private SmartConfig _config;
    private String _sessionId;
    private boolean _preconfigured;

    private SmartCommunicator(Context ctx) {
        _context = ctx;
        _preconfigured = false;
        _requestQueue = getRequestQueue();
    }

    public void setSmartServer(String ip, int p, String tenant) {
        _server = ip;
        _port = p;
        _tenant = tenant;
        _requestQueue = null;
        _preconfigured = true;
        _requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (_requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            _requestQueue = Volley.newRequestQueue(_context.getApplicationContext());

            SharedPreferences preferences = _context.getApplicationContext().getSharedPreferences("smart", Context.MODE_PRIVATE);
            //_server = preferences.getString("server", "139.59.6.59");
            if (!_preconfigured) { //defaults for debugging
                _server = preferences.getString("server", "192.168.1.33");
                _port = preferences.getInt("port", 9081);
                _tenant = preferences.getString("tenant", "traffic");
            }
            _config = new SmartConfig();
            _config.setPort(_port);
            _config.setServer(_server);
            _config.setTenant(_tenant);
        }
        return _requestQueue;
    }

    public void setSessionId(String sess) { _sessionId = sess; }

    private void useSessionId(SmartRequest request) {
        if ((_sessionId != null) && (_sessionId.length() > 0)) {
            request.setSessionId(_sessionId);
        }
    }
    public SmartRequest newRequest(String flow, String evt, SmartResponseListener listener) {
        SmartRequest request = new SmartRequest(_config, null, flow, evt, listener);
        useSessionId(request);
        return request;
    }

    public void queueSmartRequest(SmartRequest req) {
        useSessionId(req);
        getRequestQueue().add(req);
    }

    public void queueSmartRequest(String flow, String evt, String prime, Object k, Map<String, Object> parms, SmartResponseListener listener) {
        SmartRequest request = new SmartRequest(_config, null, flow, evt, listener);
        useSessionId(request);
        request.postToObject(prime, k, parms);
        queueSmartRequest(request);
    }
}
