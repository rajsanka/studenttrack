package xchg.online.studenttrack.data;

import android.app.Activity;

import org.xchg.online.baseframe.utils.Logger;

import java.util.HashMap;
import java.util.Map;

import xchg.online.studenttrack.smart.trackflow.EndTrip;
import xchg.online.studenttrack.smart.trackflow.GetTripSummary;
import xchg.online.studenttrack.smart.trackflow.StartTrip;
import xchg.online.studenttrack.utils.MapUtils;

/**
 * Created by rsankarx on 16/01/17.
 */

public class TravelData {

    public static interface OnServerCallListener {
        public void onStartedTrip(String trip);
        public void onEndedTrip(String trip);
        public void onUploadLocations(String trip);
        public void onError(String msg);
        public void onTripSummary(TripSummary summary);
    }

    private static TravelData TRAVELS = new TravelData();

    private Map<String, RouteData> mRoutes;
    private RouteData unTracked;
    private String currentRoute;
    private String deviceId;

    private Map<String, TripSummary> trips;

    private Activity currentActivity;
    private OnServerCallListener listener;

    private static final String UNKNOWN_NAME = "___travel__unknown___";

    private TravelData() {
        mRoutes = new HashMap<>();
        trips = new HashMap<>();
    }

    public static boolean isUnknown(String nm) { return nm.equals(UNKNOWN_NAME); }
    public static void setDeviceId(String dev) {
        TRAVELS.deviceId = dev;
    }

    public static String getDeviceId() {
        return TRAVELS.deviceId;
    }

    public static void setCurrentActivity(Activity act, OnServerCallListener svrlistener) {
        TRAVELS.currentActivity = act;
        TRAVELS.listener = svrlistener;
    }

    public static Activity getCurrentActivity() {
        return TRAVELS.currentActivity;
    }

    public static OnServerCallListener getListener() { return TRAVELS.listener; }

    public static RouteData startUnTracked(double lat, double lng) {
        TRAVELS.unTracked = new RouteData(UNKNOWN_NAME, "", lat, lng);
        TRAVELS.mRoutes.put(UNKNOWN_NAME, TRAVELS.unTracked);
        TRAVELS.currentRoute = UNKNOWN_NAME;
        return TRAVELS.unTracked;
    }

    public static RouteData nextUnTracked(double lat, double lng) {
        return nextRouteLocation(UNKNOWN_NAME, lat, lng);
    }

    public static void startTrip() {
        LocationData location = getCurrentLocation();
        String address = MapUtils.getAddress(TRAVELS.currentActivity, location.getLatitude(), location.getLongitude());
        StartTrip trip = new StartTrip(TRAVELS.deviceId, location, address);
        trip.postTo(TRAVELS.currentActivity);
    }

    public static void endTrip() {
        LocationData location = getCurrentLocation();
        String address = MapUtils.getAddress(TRAVELS.currentActivity, location.getLatitude(), location.getLongitude());
        EndTrip trip = new EndTrip(TRAVELS.currentRoute, location, address);
        trip.postTo(TRAVELS.currentActivity);
    }

    public static RouteData startRoute(String nm, String addr, double lat, double lng) {
        synchronized (TRAVELS) {
            TRAVELS.currentRoute = nm;
            RouteData route = null;
            if (!TRAVELS.mRoutes.containsKey(nm)) {
                route = new RouteData(nm, addr, lat, lng);
                TRAVELS.mRoutes.put(nm, route);
            } else {
                route = TRAVELS.mRoutes.get(nm);
                route.restart(addr, lat, lng);
            }
            return route;
        }
    }

    public static RouteData nextRouteLocation(String nm, double lat, double lng) {
        synchronized (TRAVELS) {
            RouteData route = null;
            if (TRAVELS.mRoutes.containsKey(nm)) {
                Logger.d(TravelData.class.getSimpleName(), "TravelData: Adding next location for: " + nm);
                route = TRAVELS.mRoutes.get(nm);
                route.appendLocation(lat, lng);
            }

            return route;
        }
    }

    public static RouteData nextRouteLocation(double lat, double lng) {
        synchronized (TRAVELS) {
            RouteData route = null;
            if ((TRAVELS.currentRoute == null) || (TRAVELS.currentRoute.length() <= 0)) {
                route = startUnTracked(lat, lng);
            } else {
                route = nextRouteLocation(TRAVELS.currentRoute, lat, lng);
            }
            return route;
        }
    }

    public static RouteData endRoute(String nm, String addr, double lat, double lng) {
        synchronized (TRAVELS) {
            TRAVELS.currentRoute = UNKNOWN_NAME;
            RouteData route = null;
            if (TRAVELS.mRoutes.containsKey(nm)) {
                route = TRAVELS.mRoutes.get(nm);
                route.endRoute(addr, lat, lng);
            }
            return route;
        }
    }

    public static LocationData getCurrentLocation() {
        synchronized (TRAVELS) {
            RouteData route = TRAVELS.mRoutes.get(TRAVELS.currentRoute);
            if (route != null)
                return route.getCurrentLocation();

            return null;
        }
    }

    public static RouteData getRoute(String nm) {
        return TRAVELS.mRoutes.get(nm);
    }

    public static TripSummary addTripSummary(Map map) {
        TripSummary summary = new TripSummary(map);
        RouteData route = getRoute(summary.getTripName());
        if (route != null) {
            TRAVELS.mRoutes.remove(route);
        }

        TRAVELS.trips.put(summary.getTripName(), summary);
        return summary;
    }

    public static void getTripSummary(String trip) {
        GetTripSummary get = new GetTripSummary(trip);
        get.postTo(TRAVELS.currentActivity);
    }

    public static RouteData trackRoute(String phone, TrackListener listener) {
        RouteData route = getRoute(phone);
        if (route == null) {
            route = new RouteData(phone, listener);
            TRAVELS.mRoutes.put(phone, route);
        } else if (!route.isValid()) {
            route.getTrackData();
        }
        return route;
    }

    public static void stopTrack(String phone) {
        RouteData route = getRoute(phone);
        if (route != null) {
            route.stopTracking();
        }
    }

    public static void resetTravels() {
        RouteData.stopRouteUploads();
        TRAVELS = new TravelData();
    }
}
