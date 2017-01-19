/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.EndTrip
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An event to end the trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

public class EndTrip implements java.io.Serializable
{
    private UploadLocations.LocationData endLocation;
    private String endAddress;

    public EndTrip()
    {
    }

    public UploadLocations.LocationData getEndLocation() { return endLocation; }
    public String getEndAddress() { return endAddress; }
}

