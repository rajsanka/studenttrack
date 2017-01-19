/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.VehicleLocation
 * Revision:            1.0
 * Date:                09-09-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A vehicle location recorded
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.UUID;

public class VehicleLocation implements java.io.Serializable
{
    private UUID id;
    private String tripName;
    private String routeName;

    private String deviceId;
    private double latitude;
    private double longitude;
    private double direction;
    private double altitude;
    private double accuracy;
    private double distance;
    private double speed;
    private long duration;
    private long startTime;

    public VehicleLocation()
    {
    }

    VehicleLocation(String t, String devId, UploadLocations.LocationData loc)
    {
        id = UUID.randomUUID();
        deviceId = devId;
        tripName = t;
        latitude = loc.latitude;
        longitude = loc.longitude;
        direction = loc.direction;
        altitude = loc.altitude;
        accuracy = loc.accuracy;
        startTime = loc.startTime;
        distance = loc.distance;
        speed = loc.speed;
        duration = loc.duration;
    }

    public UUID getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getDirection() { return direction; }
    public double getAltitude() { return altitude; }
    public double getAccuracy() { return accuracy; }
    public long getTime() { return startTime; }
    public long getDuration() { return duration; }
    public double getDistance() { return distance; }
    public double getSpeed() { return speed; }
}

