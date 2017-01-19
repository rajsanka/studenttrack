/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.TripSummary
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                31-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A summary of the trip computed at the end of the trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.UUID;

public class TripSummary implements java.io.Serializable
{
    UUID summaryId;
    String tripName;
    long durationTraveled;
    double distanceTraveled;
    double averageSpeed;
    double highestSpeed;

    long straightDistance;

    long startTime;
    long endTime;

    //private long noStops;
    //private List<TripStop> stops;

    public TripSummary(Trip tn)
    {
        summaryId = UUID.randomUUID();
        tripName = tn.getTripName();
        startTime = tn.getStartTime();
    }

    public void addedLocation(VehicleLocation loc)
    {
        distanceTraveled += loc.getDistance();
        endTime = loc.getTime();
        durationTraveled = (endTime - startTime) / 1000; //in seconds
        if (durationTraveled > 0) averageSpeed = distanceTraveled / durationTraveled; //in meters / sec
        if (loc.getSpeed() > highestSpeed) highestSpeed = loc.getSpeed();
    }
}

