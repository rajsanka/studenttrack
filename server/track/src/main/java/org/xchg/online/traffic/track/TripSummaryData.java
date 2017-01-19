/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.TripSummaryData
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

public class TripSummaryData implements java.io.Serializable
{
    private String tripName;
    private long durationTraveled;
    private double distanceTraveled;
    private double averageSpeed;
    private double highestSpeed;
    private String origin;
    private String destination;

    private long straightDistance;

    private long startTime;
    private long endTime;

    private List<UploadLocations.LocationData> route;
    
    public TripSummaryData(TripSummary summ, Trip t, List<VehicleLocation> locs)
    {
        setupSummary(summ, t);
        route = new ArrayList<UploadLocations.LocationData>();
        for (int i = 0; i < locs.size(); i++)
        {
            addLocation(locs.get(i));
        }
    }

    private void setupSummary(TripSummary summ, Trip t)
    {
        tripName = summ.tripName;
        durationTraveled = summ.durationTraveled;
        distanceTraveled = summ.distanceTraveled;
        averageSpeed = summ.averageSpeed;
        highestSpeed = summ.highestSpeed;

        straightDistance = summ.straightDistance;

        startTime = summ.startTime;
        endTime = summ.endTime;

        origin = t.getStartAddress();
        destination = t.getEndAddress();
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

