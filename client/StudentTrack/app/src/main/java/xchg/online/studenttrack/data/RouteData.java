package xchg.online.studenttrack.data;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import xchg.online.studenttrack.smart.trackflow.GetCurrentTrip;
import xchg.online.studenttrack.smart.trackflow.GetCurrentTripLocation;
import xchg.online.studenttrack.smart.trackflow.UploadLocations;
import xchg.online.studenttrack.utils.TimerUtil;

/**
 * Created by rsankarx on 16/01/17.
 */

public class RouteData {
    private String name;
    private String mStartAddress;
    private String mEndAddress;
    private long mStartTime;
    private long mEndTime;
    private LocationData currentLocation;
    private List<LocationData> mLocations;

    private UploadLocations upload;
    private UploadRoute uploader;
    private TrackRoute tracker;

    private boolean tracking;
    private String trackPhone;
    private TrackListener trackListener;

    private static final String ROUTEUPLOAD = "ROUTEUPLOAD";
    private static final String TRACKROUTE = "TRACKROUTE";

    public RouteData(String nm, String addr, double lat, double lng) {
        name = nm;
        tracking = false;
        if (!isUnTracked()) {
            upload = new UploadLocations(TravelData.getDeviceId(), nm);
            uploader = new UploadRoute(TravelData.getCurrentActivity(), this);
            TimerUtil.startTimer(ROUTEUPLOAD, 30000, 30000, uploader);
        }
        restart(addr, lat, lng);
    }

    public RouteData(String track, TrackListener listener) {
        mLocations = new ArrayList<>();
        tracking = true;

        trackPhone = track;
        trackListener = listener;

        getTrackData();
        //TimerUtil.startTimer(TRACKROUTE + track, 30000, 30000, tracker);
    }

    public void getTrackData() {
        GetCurrentTrip get = new GetCurrentTrip(trackPhone);
        get.postTo(TravelData.getCurrentActivity(), new OnLocationListener(this, trackListener));
        tracker = new TrackRoute(trackPhone, this, trackListener);
    }

    public void startTracking() {
        TimerUtil.startTimer(TRACKROUTE + trackPhone, 30000, 30000, tracker);
    }

    public void stopTracking() {
        TimerUtil.stopTimer(TRACKROUTE + trackPhone);
    }

    public boolean isValid() {
        return (name != null);
    }

    public boolean isUnTracked() {return TravelData.isUnknown(name); }

    public void appendLocation(double lat, double lng) {
        LocationData prev = null;
        if (mLocations.size() > 0) {
            prev = mLocations.get(mLocations.size() - 1);
        }
        LocationData d = LocationData.getLocation(lat, lng, prev);
        mLocations.add(d);
        currentLocation = d; //The latest is the current location
        if (!tracking && !isUnTracked()) upload.addLocation(d);
    }

    public void endRoute(String addr, double lat, double lng) {
        appendLocation(lat, lng);
        mEndAddress = addr;
        mEndTime = System.currentTimeMillis();
        if (!isUnTracked()) TimerUtil.stopTimer(ROUTEUPLOAD);
    }

    public List<LocationData> getLocations() { return mLocations; }

    public void restart(String addr, double lat, double lng) {
        mStartAddress = addr;
        mStartTime = System.currentTimeMillis();
        mLocations = new ArrayList<>();
        appendLocation(lat, lng);
    }

    public LocationData getCurrentLocation() { return currentLocation; }

    public static class UploadRoute implements Runnable {
        private Activity activity;
        private RouteData routeData;

        UploadRoute(Activity act, RouteData route)
        {
            activity = act;
            routeData = route;
        }

        public void run() {
            routeData.upload.postTo(activity);
        }
    }

    public static class OnLocationListener implements GetCurrentTripLocation.TripLocationListener {

        RouteData route;
        TrackListener listener;

        OnLocationListener(RouteData r, TrackListener l) {
            route = r;
            listener = l;
        }

        public void doneRead() {
            listener.doneRead();
        }

        public void onNoTrip(String msg) {
            route.stopTracking();
            listener.onError("No Trip Found");
        }

        @Override
        public void onTripName(String nm) {
            route.name = nm;
            route.startTracking();
        }

        @Override
        public void onLocation(LocationData locationDatas) {
            route.appendLocation(locationDatas.getLatitude(), locationDatas.getLongitude());
            listener.onLocationChanged(locationDatas);
        }

        @Override
        public void onError(String msg) {
            listener.onError(msg);
        }
    }

    public static class TrackRoute implements Runnable {

        private String phone;
        private TrackListener listener;
        private RouteData route;

        TrackRoute(String track, RouteData r, TrackListener l) {
            phone = track;
            route = r;
            listener = l;
        }

        public void run() {
            GetCurrentTripLocation get = new GetCurrentTripLocation(phone);
            get.postTo(TravelData.getCurrentActivity(), new OnLocationListener(route, listener));
        }
    }

    public static void stopRouteUploads() {
        TimerUtil.stopAll();
    }
}
