/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.UploadLocations
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                24-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of locations uploaded
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.List;

public class UploadLocations implements java.io.Serializable
{
    public static class LocationData implements java.io.Serializable
    {
        double latitude;
        double longitude;
        double direction;
        double altitude;
        double accuracy;
        long startTime;
        double distance;
        double speed;
        long duration;
    }

    private String deviceId;
    private List<LocationData> locations;

    public UploadLocations()
    {
    }

    public String getDeviceId() { return deviceId; }
    public List<LocationData> getLocations() { return locations; }
}

