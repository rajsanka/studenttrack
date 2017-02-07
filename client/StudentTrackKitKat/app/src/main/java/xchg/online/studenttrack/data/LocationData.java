package xchg.online.studenttrack.data;

import org.xchg.online.baseframe.utils.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import xchg.online.studenttrack.utils.MapUtils;

/**
 * Created by rsankarx on 16/01/17.
 */

public class LocationData {
    private double mLatitude;
    private double mLongitude;
    private double distance;
    private double speed;
    private long duration;
    private long startTime;

    private LocationData(double lat, double lng) {
        mLatitude = lat;
        mLongitude = lng;
        startTime = System.currentTimeMillis();
        duration = 0;
        speed = 0;
        distance = 0;
    }

    public static double precisionDouble(double toBeTruncated) {
        Double truncatedDouble = BigDecimal.valueOf(toBeTruncated)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return truncatedDouble;
    }

    private LocationData(double lat, double lng, LocationData prev) {
        this(lat, lng);
        duration = (startTime - prev.getStartTime()) / 1000000; //in millisecs
        distance = precisionDouble(MapUtils.calculateDistance(lat, lng, prev.getLatitude(), prev.getLongitude())); //in meters
        double insecs = duration / 1000;
        if (insecs > 0) speed = precisionDouble(distance / insecs); // in meters/sec
    }

    LocationData(Map map) {
        mLatitude = (double) map.get("latitude");
        mLongitude = (double) map.get("longitude");
        startTime = Utilities.getLong(map.get("startTime"));
        distance = (double) map.get("distance");
        speed = (double) map.get("speed");
        duration = Utilities.getLong(map.get("duration"));
    }

    public double getLatitude() { return  mLatitude; }
    public double getLongitude() { return mLongitude; }
    public long getStartTime() {return startTime; }
    public long getDuration() {return duration; }
    public double getDistance() { return distance; }
    public double getSpeed() { return speed; }

    public static LocationData getLocation(double lat, double lng, LocationData previous) {
        LocationData ret;
        if (previous != null) {
            ret = new LocationData(lat, lng, previous);
        } else {
            ret = new LocationData(lat, lng);
        }

        return ret;
    }

    public static LocationData getLocation(double lat, double lng, long time, LocationData previous) {
        LocationData ret;
        if (previous != null) {
            ret = new LocationData(lat, lng, previous);
        } else {
            ret = new LocationData(lat, lng);
        }

        ret.startTime = time;

        return ret;
    }
}
