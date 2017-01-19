/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.Route
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A route against which the locations are tracked
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

public class Route implements java.io.Serializable
{
    private String routeName;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private long typicalStartTime;
    private String startAddress;
    private String endAddress;

    public Route(String nm)
    {
        routeName = nm;
    }

    void setStart(double lat, double lng, String addr, long time)
    {
        startLatitude = lat;
        startLongitude = lng;
        startAddress = addr;
    }

    void setEnd(double lat, double lng, String addr)
    {
        endLatitude = lat;
        endLongitude = lng;
        endAddress = addr;
    }

    public String getRouteName() { return routeName; }

}

