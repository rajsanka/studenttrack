package org.anon.smart.client;

import android.app.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 14/09/16.
 */
public class SmartSecurity {

    public static class SessionIdListener implements SmartResponseListener {
        private SmartResponseListener _custom;
        private Activity _activity;

        SessionIdListener(Activity act, SmartResponseListener listener) {
            _custom = listener;
            _activity = act;
        }

        public void handleResponse(List responses){
            Map val = (Map) responses.get(0);
            SmartSecurity._lastSessionId = (String) val.get("sessionId");
            SmartCommunicator.getInstance(_activity).setSessionId(_lastSessionId);
            _custom.handleResponse(responses);
        }

        public void handleError(double code, String context){
            _custom.handleError(code, context);
        }

        public void handleNetworkError(String message){
            _custom.handleNetworkError(message);
        }
    }

    public static class LogoutListener implements SmartResponseListener {
        private SmartResponseListener _custom;
        private Activity _activity;

        LogoutListener(Activity act, SmartResponseListener listener) {
            _custom = listener;
            _activity = act;
        }

        public void handleResponse(List responses){
            SmartSecurity._lastSessionId = null;
            SmartCommunicator.getInstance(_activity).setSessionId(null);
        }

        public void handleError(double code, String context){
            _custom.handleError(code, context);
        }

        public void handleNetworkError(String message){
            _custom.handleNetworkError(message);
        }
    }

    public static interface SessionCheckListener {
        public void handleValidSession();
        public void handleInvalidSession();
        public void handleNetworkError(String msg);
    }

    public static class ValidSessionListener implements SmartResponseListener {
        private SessionCheckListener _custom;
        private Activity _activity;
        private String sessiondId;

        ValidSessionListener(Activity act, String sessId, SessionCheckListener listener) {
            _custom = listener;
            _activity = act;
            sessiondId = sessId;
        }

        public void handleResponse(List responses){

            SmartSecurity._lastSessionId = (String) sessiondId;
            SmartCommunicator.getInstance(_activity).setSessionId(_lastSessionId);
            _custom.handleValidSession();
        }

        public void handleError(double code, String context){
            SmartSecurity._lastSessionId = null;
            SmartCommunicator.getInstance(_activity).setSessionId(null);
            _custom.handleInvalidSession();
        }

        public void handleNetworkError(String message){
            _custom.handleNetworkError(message);
        }
    }

    public static class PermittedFeaturesListener implements SmartResponseListener {
        private FeaturesListener _custom;
        private Activity _activity;

        PermittedFeaturesListener(Activity act, FeaturesListener listener) {
            _custom = listener;
            _activity = act;
        }

        public void handleResponse(List responses) {
            Map access = (Map) responses.get(0);

            String roleName = access.get("roleName").toString();
            _custom.handleRoleName(roleName);
            Map features = (Map)access.get("features");
            Boolean allpermitted = (Boolean) access.get("allPermitted");
            if (allpermitted == null) allpermitted = Boolean.FALSE;

            for (Object feature : features.keySet()) {
                _custom.handleFeature(feature.toString(), features.get(feature).toString());
            }

            if (allpermitted) {
                _custom.handleAllPermitted();
            }

            _custom.handleResponse(responses);
        }

        public void handleError(double code, String context){
            _custom.handleError(code, context);
        }

        public void handleNetworkError(String message){
            _custom.handleNetworkError(message);
        }
    }

    public static interface FeaturesListener extends SmartResponseListener {
        public void handleRoleName(String roleName);
        public void handleAllPermitted();
        public void handleFeature(String feature, String permit);
    }

    private static String _lastSessionId;

    public static String getLastSessionId() { return _lastSessionId; }

    public static void autheticate(Activity activity, String user, String password, SmartResponseListener listener) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("identity", user);
        parms.put("password", password);
        parms.put("type", "custom");
        SmartCommunicator.getInstance(activity).queueSmartRequest("Security", "Authenticate",
                "FlowAdmin", "Security", parms, new SessionIdListener(activity, listener));
    }

    public static void getPermittedFeatures(Activity activity, FeaturesListener listener) {
        Map<String, Object> parms = new HashMap<>();
        SmartCommunicator.getInstance(activity).queueSmartRequest("Security", "GetPermittedFeatures",
                "FlowAdmin", "Security", parms, new PermittedFeaturesListener(activity, listener));

    }

    public static void logout(Activity activity, SmartResponseListener listener) {
        if (_lastSessionId != null) {
            Map<String, Object> parms = new HashMap<>();
            SmartCommunicator.getInstance(activity).queueSmartRequest("Security", "Logout",
                    "Session", _lastSessionId, parms, new LogoutListener(activity, listener));
        }
    }

    public static void validSession(Activity activity, String sessId, SessionCheckListener listener) {
        if (sessId != null) {
            Map<String, Object> parms = new HashMap<>();
            SmartCommunicator.getInstance(activity).queueSmartRequest("Security", "SessionValid",
                    "Session", sessId, parms, new ValidSessionListener(activity, sessId, listener));
        }
    }
}
