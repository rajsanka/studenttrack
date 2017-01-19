/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.Device
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                24-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A device against which locations are recorded
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

public class Device implements java.io.Serializable
{
    private String deviceId;
    private String phoneNumber;
    private boolean isRoaming;
    private String country;
    private String currentTrip;

    public Device()
    {
    }

    public Device(CreateDevice d)
    {
        deviceId = d.deviceId;
        phoneNumber = d.phoneNumber;
        isRoaming = d.isRoaming;
        country = d.country;
    }

    public String getDeviceId() { return deviceId; }

    public void setCurrentTrip(String trip)
    {
        currentTrip = trip;
    }

    public String getCurrentTrip() { return currentTrip; }
}

