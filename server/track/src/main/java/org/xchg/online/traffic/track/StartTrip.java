/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.StartTrip
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * start a trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class StartTrip implements java.io.Serializable
{
    private UploadLocations.LocationData startLocation;
    private String startAddress;

    private transient Map query;
    private transient List running;

    public StartTrip()
    {
    }

    public UploadLocations.LocationData getStartLocation() { return startLocation; }
    public String getStartAddress() { return startAddress; }

    public void setupSearch(Device dev)
    {
        query = new HashMap();
        query.put("deviceId", dev.getDeviceId());
        query.put("tripStatus", "started");
        running = new ArrayList();
    }

    public List getRunning() { return running; }
}

