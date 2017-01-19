/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.CreateDevice
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                24-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * To create a new device
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

public class CreateDevice implements java.io.Serializable
{
    String deviceId;
    String phoneNumber;
    boolean isRoaming;
    String country;

    public CreateDevice()
    {
    }
}

