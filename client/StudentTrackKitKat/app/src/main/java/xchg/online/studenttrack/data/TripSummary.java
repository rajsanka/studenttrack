package xchg.online.studenttrack.data;

import org.xchg.online.baseframe.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 16/01/17.
 */

public class TripSummary {
    private String tripName;
    private long durationTraveled;
    private double distanceTraveled;
    private double averageSpeed;
    private double highestSpeed;
    private String origin;
    private String destination;

    private long straightDistance;

    private long startTime;
    private long endTime;

    private String startDate;
    private String endDate;

    private List<LocationData> route;

    public TripSummary() {
        tripName = "Test";
        durationTraveled = 65*60;
        distanceTraveled = 1057;
        averageSpeed = 200;
        highestSpeed = 250;

        origin = "314, 2F Main, 11th Block, 2nd Stage, Nagarbhavi, Bangalore - 560072";
        destination = "314, 2F Main, 11th Block, 2nd Stage, Nagarbhavi, Bangalore - 560072";


        startDate = Utilities.convertTime(System.currentTimeMillis() - durationTraveled);
        endDate = Utilities.convertTime(System.currentTimeMillis());
    }


    public TripSummary(Map response) {
        tripName = response.get("tripName").toString();
        Object obj = response.get("durationTraveled");
        durationTraveled = Utilities.getLong(obj);
        distanceTraveled = (double) response.get("distanceTraveled");
        averageSpeed = (double) response.get("averageSpeed");
        highestSpeed = (double) response.get("highestSpeed");
        origin = response.get("origin").toString();
        destination = response.get("destination").toString();

        startTime = Utilities.getLong(response.get("startTime"));
        endTime = Utilities.getLong(response.get("endTime"));

        startDate = Utilities.convertTime(startTime);
        endDate = Utilities.convertTime(endTime);

        List locs = (List) response.get("route");
        route = new ArrayList<LocationData>();
        for (int i = 0; (locs != null) && (i < locs.size()); i++) {
            Map m = (Map) locs.get(i);
            LocationData l = new LocationData(m);
            route.add(l);
        }
    }

    public String getTripName() { return tripName; }
    public String getDurationTraveled() {
        return Utilities.convertDuration(durationTraveled);
    }

    public String getDistanceTraveled() { return Utilities.convertDistance(distanceTraveled); }
    public String getAverageSpeed() { return averageSpeed + "m/s"; }
    public String getHighestSpeed() { return highestSpeed + "m/s"; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }

    public List<LocationData> getRoute() { return route; }
}
