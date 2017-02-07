/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.GetOpenTrips
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                31-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * Gets that summary for the trip
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class GetOpenTrips implements java.io.Serializable
{
    private List<Trip> trips;
    private Map search;

    public GetOpenTrips()
    {
    }

    void setupSearch()
    {
        search = new HashMap();
        search.put("tripStatus", "started");
        trips = new ArrayList();
    }

    public List getTrips() { return trips; } 
}

