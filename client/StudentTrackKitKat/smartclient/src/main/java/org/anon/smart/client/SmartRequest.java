package org.anon.smart.client;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by rsankarx on 14/09/16.
 */
public class SmartRequest extends Request<SmartResponse> {

    public static class SmartErrorListener implements Response.ErrorListener {

        private SmartResponseListener _mylistener;

        public SmartErrorListener(SmartResponseListener req) {
            _mylistener = req;
        }

        public void onErrorResponse(VolleyError error) {
            Log.e("SmartCommunicator", "Error in communication " + error);
            if (_mylistener != null) {
                if ((error.networkResponse == null) && (error.getCause() != null)) {
                    _mylistener.handleNetworkError(error.getCause().getMessage());
                }
                else if (error.networkResponse != null) {
                    _mylistener.handleNetworkError(error.networkResponse.toString());
                }
            }
        }
    }

    private String _tenant;
    private String _flow;
    private String _event;
    private String _sessionId;
    private String _prime;
    private Object _primeKey;
    private Map<String, Object> _parms;

    private SmartResponseListener _listener;

    private static final String  SMART_ACTION_LOOKUP = "lookup";
    private static final String  FLOW_ADMIN = "FlowAdmin";
    private static final String  TENANT_ADMIN = "TenantAdmin";
    private static final String  SMART_OWNER = "SmartOwner";
    private static final String  ADMIN_SMART_FLOW = "AdminSmartFlow";
    private static final String  SECURITY_FLOW = "Security";

    private static final String  SEARCH_EVENT = "SearchEvent";
    private static final String  LOOKUP_EVENT = "LookupEvent";
    private static final String  LISTALL_EVENT = "ListAllEvent";
    private static final String  CREATE_PRIME_EVENT = "CreatePrime";
    private static final String  UPDATE_PRIME_EVENT = "UpdatePrime";

    private static final String  GET_JAVASCRIPT_EVENT = "GetJavaScript";
    private static final String  CHECK_EXISTENCE_EVENT = "CheckExistence";

    public SmartRequest(SmartConfig cfg, String tenant, String flow, String event, SmartResponseListener listener) {
        super(Request.Method.POST, cfg.getPostUrl(tenant, flow, event), new SmartErrorListener(listener));
        _tenant = tenant;
        _flow = flow;
        _event = event;
        _listener = listener;
    }

    public SmartRequest(SmartConfig cfg, String tenant, String flow, String event, String sess, SmartResponseListener listener) {
        this(cfg, tenant, flow, event, listener);
        _sessionId = sess;
        if ((_sessionId == null) || (_sessionId.length() <= 0)) throw new AssertionError("Cannot access without session Id");
    }

    public void setSessionId(String sess) { _sessionId = sess; }

    public void postToObject(String prime, Object key, Map<String, Object> parms) {
        _prime = prime;
        _primeKey = key;
        _parms = parms;

        Map<String, Object> postTo = new HashMap<>();
        postTo.put("___smart_action___", SMART_ACTION_LOOKUP);
        postTo.put("___smart_value___", _primeKey);

        _parms.put(_prime, postTo);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> hdrs = new HashMap<>();
        if ((_sessionId != null) && (_sessionId.length() > 0)) {
            hdrs.put("Session-Id", _sessionId);
        }
        hdrs.put("Accept-Encoding", "gzip");
        hdrs.put("Content-Encoding", "gzip");
        return hdrs;
    }

    public String getBodyContentType() {
        return "application/json;";
    }

    public byte[] getBody() throws AuthFailureError {
        Gson gson = new Gson();
        String post = gson.toJson(_parms);
        Log.d(this.getClass().getSimpleName(), "Posting: " + post);
        byte[] jsonbytes = post.getBytes();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gin = new GZIPOutputStream(out);
            gin.write(jsonbytes);
            gin.close();

            jsonbytes = out.toByteArray();
            out.close();
        } catch (Exception e) {
            throw new AuthFailureError(e.getMessage());
        }

        return jsonbytes;
    }

    protected void deliverResponse(SmartResponse response) {
        Log.i("SmartRequest", "Called deliverResponse " + response);

        if (response.hasError()) {
            _listener.handleError(response.getError().getCode(), response.getError().getContext());
        } else {
            _listener.handleResponse(response.getResponses());
        }
    }

    protected Response<SmartResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            Log.i("SmartRequest", "Called parseNetworkResponse " + response);

            StringBuffer buffer = new StringBuffer();
            try {
                GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
                InputStreamReader reader = new InputStreamReader(gStream);
                BufferedReader in = new BufferedReader(reader);
                String read;
                while ((read = in.readLine()) != null) {
                    buffer.append(read);
                }
                reader.close();
                in.close();
                gStream.close();
            } catch (Exception e) {
                return Response.error(new ParseError());
            }

            String json = new String(
                    buffer.toString().getBytes(),
                    HttpHeaderParser.parseCharset(response.headers));

            Gson gson = new Gson();
            Map resp = gson.fromJson(json, Map.class);

            SmartResponse sresponse = new SmartResponse(resp);
            /*if (sresponse.hasError()) {
                VolleyError error = new VolleyError(sresponse.getError().toString());
                return Response.error(error);
            }*/
            return Response.success(sresponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
