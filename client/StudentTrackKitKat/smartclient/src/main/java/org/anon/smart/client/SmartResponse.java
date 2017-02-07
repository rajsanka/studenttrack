package org.anon.smart.client;

import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 14/09/16.
 */
public class SmartResponse {

    public static class SmartError {
        private double code;
        private String context;

        public double getCode() { return code; }
        public String getContext() { return context; }

        public String toString() {
            return code + ":" + context; //replace this with globalization
        }
    }

    private static final String ERRORS = "errors";
    private static final String RESPONSES = "responses";

    private List _responses;
    private SmartError _error;
    private boolean _hasError = false;

    public SmartResponse(Map resp) {
        _hasError = handleIfErrorPresent(resp);

        if (resp.containsKey(RESPONSES)) {
            List responses = (List)resp.get(RESPONSES);
            Map firstresp = (Map) responses.get(0);
            _hasError = handleIfErrorPresent(firstresp);
            if (!_hasError) {
                _responses = responses;
            }
        }
    }

    public boolean hasError() { return _hasError; }
    public List getResponses() { return _responses; }
    public SmartError getError() { return _error; }

    private boolean handleIfErrorPresent(Map resp) {
        boolean err = false;
        if (resp.containsKey(ERRORS)) {
            List lst = (List) resp.get(ERRORS);
            Map error = (Map) lst.get(0);
            err = true;
            _error = new SmartError();
            _error.code = (Double) error.get("code");
            _error.context = (String) error.get("context");
        }

        return err;
    }
}
