package xchg.online.studenttrack.data;

/**
 * Created by rsankarx on 17/01/17.
 */

public interface TrackListener {
    public void doneRead();
    public void onLocationChanged(LocationData locationData);
    public void onError(String msg);
}
