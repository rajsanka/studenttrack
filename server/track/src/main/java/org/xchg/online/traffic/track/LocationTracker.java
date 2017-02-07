/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.LocationTracker
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                24-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A tracker for locations uploaded
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

import java.util.List;

public class LocationTracker
{
    public LocationTracker()
    {
    }

    public void startTrip(StartTrip start, Device dev)
    {
        UploadLocations.LocationData sLoc = start.getStartLocation();
        Trip thistrip = new Trip(dev.getDeviceId(), sLoc.startTime);
        thistrip.setStartLocation(sLoc.latitude, sLoc.longitude, start.getStartAddress());
        TripSummary summary = new TripSummary(thistrip);
        VehicleLocation l1 = new VehicleLocation(thistrip.getTripName(), dev.getDeviceId(), sLoc);
        dev.setCurrentTrip(thistrip.getTripName());

        //setup search so that the open trips can be closed.
        start.setupSearch(dev);

        new UploadResponse("Started Trip", thistrip.getTripName());
    }

    public void endPrevTrip(StartTrip start)
    {
        //if there is an already existing open trip, end it?
        List running = start.getRunning();
        for (int i = 0; (running != null) && (i < running.size()); i++) 
            ((Trip)running.get(i)).endWithLatest();
    }

    public void uploadLocations(UploadLocations locations, Trip trip, TripSummary summary)
    {
        List<UploadLocations.LocationData> locs = locations.getLocations();
        System.out.println("Creating locations for: " + locs);
        for (int i = 0; i < locs.size(); i++)
        {
            UploadLocations.LocationData l = locs.get(i);
            VehicleLocation l1 = new VehicleLocation(trip.getTripName(), locations.getDeviceId(), l);
            summary.addedLocation(l1);
            trip.setLastLocation(l1);
        }

        new UploadResponse("Created Locations.", trip.getTripName());
    }

    public void endTrip(EndTrip end, Trip running, VehicleLocation lastloc, TripSummary summary)
    {
        if (end.getEndLocation() != null)
        {
            UploadLocations.LocationData l = end.getEndLocation();
            running.setEndLocation(l.latitude, l.longitude, end.getEndAddress(), l.startTime);
            VehicleLocation l1 = new VehicleLocation(running.getTripName(), running.getDeviceId(), l);
            summary.addedLocation(l1);
        }
        else
        {
            running.endWithLatest(lastloc, end.getEndAddress());
        }
        new UploadResponse("Ended Trip", running.getTripName());
    }

    public void createDevice(CreateDevice cdev, Device exist)
    {
        //While this is currently using the IMEI number, we need to find how to make this
        //independent of the user's device? Should we track this against the profile id?
        if (exist == null)
        {
            Device dev = new Device(cdev);
        }

        new UploadResponse("Created Device.", "");
    }

    public void setupSearch(GetTripSummary get, Trip t)
    {
        get.setupSearch(t);
    }

    public void getTripSummary(GetTripSummary get, Trip t, TripSummary summary)
    {
        TripSummaryData ret = new TripSummaryData(summary, t, get.getLocations());
    }

    public boolean setupCurrentTripLocations(GetCurrentTripLocation evt, Trip t)
    {
        if (!t.hasEnded())
        {
            evt.setupSearch(t);
            return true;
        }

        return false;
    }

    public boolean getLocationForCurrentTrip(GetCurrentTripLocation evt, Device dev, Trip t)
    {
        if (!t.hasEnded())
        {
            TripData data = new TripData(t, evt.getLocations());
            return true;
        }

        new UploadResponse("No trips found for the device: " + dev.getDeviceId(), dev.getDeviceId());
        return true;

        /*
        if ((t != null) && !t.hasEnded())
        {
            TripData udata = new TripData(t);
            return;
        }

        new UploadResponse("No trips found for the device: " + dev.getDeviceId(), dev.getDeviceId());
        */
    }

    public boolean setupCurrentTrip(GetCurrentTrip evt, Trip t)
    {
        if (!t.hasEnded())
        {
            evt.setupSearch(t);
            return true;
        }

        return false;
    }

    public boolean getCurrentTrip(GetCurrentTrip evt, Device dev, Trip t)
    {
        if (!t.hasEnded())
        {
            TripData data = new TripData(t, evt.getLocations());
            return true;
        }

        new UploadResponse("No trips found for the device: " + dev.getDeviceId(), dev.getDeviceId());
        return true;
    }

    public void setupOpenTripsSearch(GetOpenTrips evt)
    {
        evt.setupSearch();
    }

    public void getOpenTrips(GetOpenTrips evt)
        throws Exception
    {
        List trips = evt.getTrips();
        OpenTripData td = new OpenTripData(trips);
    }
}

