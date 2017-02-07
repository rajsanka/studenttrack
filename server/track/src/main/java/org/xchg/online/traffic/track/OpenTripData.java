/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.OpenTripData
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

public class OpenTripData implements java.io.Serializable
{
    class TripData implements java.io.Serializable
    {
        private String tripName;
        private double latitude;
        private double longitude;
        private long timeRecorded;
        private String deviceId;
        private String origin;
        private long startTime;
        private int trackCount;

        TripData(Trip t)
        {
            tripName = t.getTripName();
            latitude = t.getLastLatitude();
            longitude = t.getLastLongitude();
            startTime = t.getStartTime();
            deviceId = t.getDeviceId();
            origin = t.getStartAddress();
            trackCount = 0;
            timeRecorded = t.getLastTime();
        }
    }

    private List<TripData> trips;
    
    public OpenTripData(List lst)
    {
        trips = new ArrayList<TripData>();
        System.out.println("Got Trips as: " + lst);
        for (int i = 0; i < lst.size(); i++)
        {
            TripData td = new TripData((Trip)lst.get(i));
            trips.add(td);
        }
    }

}

