/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.TripStop
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A stop in a trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.UUID;

public class TripStop implements java.io.Serializable
{
    private UUID stopId;
    private String tripName;
    private double latitude;
    private double longitude;
    private long startTime;
    private long endTime;
    private long duration;

    public TripStop()
    {
    }
}

