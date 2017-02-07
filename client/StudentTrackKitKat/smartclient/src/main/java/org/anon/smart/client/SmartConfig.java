package org.anon.smart.client;

/**
 * Created by rsankarx on 14/09/16.
 */
public class SmartConfig {
    private String _server;
    private int _port;
    private String _protocol = "http";
    private String _tenant;

    public SmartConfig() {
        _server = "localhost";
        _port = 9081;
        _protocol = "http";
        _tenant = "test";
    }

    public void setServer(String svr) { _server = svr; }
    public void setPort(int p) { _port = p; }
    public void setProtocol(String prot) { _protocol = prot; }
    public void setTenant(String t) { _tenant = t; }

    public String getPostUrl(String ten, String flow, String evt) {
        String tenant = _tenant;
        if ((ten != null) && (ten.length() > 0))
            tenant = ten;

        if ((tenant == null) || (tenant.length() <= 0)) throw new AssertionError("Cannot access without a tenant.");
        if ((flow == null) || (flow.length() <= 0)) throw new AssertionError("Cannot access without a flow");
        if ((evt == null) || (evt.length() <= 0)) throw new AssertionError("Cannot access without event");
        if ((_server == null) || (_server.length() <= 0)) throw new AssertionError("Cannot access server");

        return _protocol + "://" + _server + ":" + _port + "/" + tenant + "/" + flow + "/" + evt;
    }
}
