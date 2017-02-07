package xchg.online.studenttrack.utils;

import xchg.online.studenttrack.data.TripSummary;

/**
 * Created by rsankarx on 16/01/17.
 */

public interface OnTripSaveListener {
    public void onSave(String name, TripSummary summary);
    public void onCancel(TripSummary summary);
}
