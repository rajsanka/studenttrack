package org.anon.smart.client;

import java.util.List;

/**
 * Created by rsankarx on 14/09/16.
 */
public interface SmartResponseListener {
    public void handleResponse(List responses);
    public void handleError(double code, String context);
    public void handleNetworkError(String message);
}
