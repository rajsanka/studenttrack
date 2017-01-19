/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.GetTripSummary
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

public class GetTripSummary implements java.io.Serializable
{
    private List<VehicleLocation> locations;
    private Map search;

    public GetTripSummary()
    {
    }

    void setupSearch(Trip t)
    {
        search = new HashMap();
        search.put("tripName", t.getTripName());
        locations = new ArrayList();
    }

    public List getLocations() { return locations; } 
}

