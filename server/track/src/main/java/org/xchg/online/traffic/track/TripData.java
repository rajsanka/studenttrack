/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.TripData
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                31-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * Retrieves the summary of the trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.List;
import java.util.ArrayList;

public class TripData implements java.io.Serializable
{
    private String tripName;
    private long startTime;
    private double lastLatitude;
    private double lastLongitude;
    private long lastTime;

    private List<UploadLocations.LocationData> route;
    
    public TripData(Trip t, List<VehicleLocation> locs)
    {
        tripName = t.getTripName();
        startTime = t.getStartTime();
        route = new ArrayList<UploadLocations.LocationData>();
        for (int i = 0; i < locs.size(); i++)
        {
            addLocation(locs.get(i));
        }
    }

    public TripData(Trip t)
    {
        tripName = t.getTripName();
        startTime = t.getStartTime();
        lastLatitude = t.getLastLatitude();
        lastLongitude = t.getLastLongitude();
        lastTime = t.getLastTime();
    }

    private void addLocation(VehicleLocation loc)
    {
        UploadLocations.LocationData data = new UploadLocations.LocationData();
        data.latitude = loc.getLatitude();
        data.longitude = loc.getLongitude();
        data.startTime = loc.getTime();
        data.duration = loc.getDuration();
        data.distance = loc.getDistance();
        data.speed = loc.getSpeed();
        route.add(data);
    }
}

