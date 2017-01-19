/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.Trip
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A route tracked for a given trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.UUID;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

public class Trip implements java.io.Serializable
{
    private String tripName;    //A unique name generated and associated with this trip.
    private String deviceId;    //The device that is tracking this trip
    private String routeName;   //The route associated with this trip

    private double startLatitude;   //The latitude and longitude of the start location of trip
    private double startLongitude;
    private String startAddress;    //The start address of the trip

    private double endLatitude;     //The end latitude and longitude of the trip
    private double endLongitude;
    private String endAddress;      //The end address of the trip
    private String tripStatus;

    private double lastLatitude;      //The latest location uploaded
    private double lastLongitude;      //The latest location uploaded
    private long lastTime;      //The latest location uploaded

    private long startTime;
    private long endTime;


    public Trip(String did, long stime)
    {
        tripName = RandomStringUtils.randomAlphanumeric(10);
        deviceId = did;
        startTime = stime;
        tripStatus = "started";
    }

    public void setStartLocation(double lat, double lng, String addr)
    {
        startLatitude = lat;
        startLongitude = lng;
        startAddress = addr;
    }

    public void setEndLocation(double lat, double lng, String addr, long time)
    {
        endLatitude = lat;
        endLongitude = lng;
        endAddress = addr;
        endTime = time;
        tripStatus = "Ended";
    }

    public void endWithLatest()
    {
        setEndLocation(lastLatitude, lastLongitude, "Not Computed", lastTime);
    }

    public void endWithLatest(VehicleLocation location, String addr)
    {
        String address = addr;
        if (address == null) address = "Not Computed";
        if (location != null) setEndLocation(location.getLatitude(), location.getLongitude(), address, location.getTime());
    }

    public Route createRoute(String name)
    {
        routeName = name;
        Route r = new Route(name);
        r.setStart(startLatitude, startLongitude, startAddress, startTime);
        r.setEnd(endLatitude, endLongitude, endAddress);
        return r;
    }

    public Route modifyRoute(Route r)
    {
        routeName = r.getRouteName();
        //TODO: process the start and end and time
        return r;
    }

    public void setLastLocation(VehicleLocation loc) 
    { 
        lastLatitude = loc.getLatitude(); 
        lastLongitude = loc.getLongitude(); 
        lastTime = loc.getTime(); 
    }

    public String getTripName() { return tripName; }
    public String getDeviceId() { return deviceId; }
    public long getStartTime() { return startTime; }
    public String getStartAddress() { return startAddress; }
    public String getEndAddress() { return endAddress; }

    public boolean hasEnded() { return tripStatus.equals("Ended"); }

    public double getLastLatitude() { return lastLatitude; }
    public double getLastLongitude() { return lastLongitude; }
    public long getLastTime() { return lastTime; }
}

