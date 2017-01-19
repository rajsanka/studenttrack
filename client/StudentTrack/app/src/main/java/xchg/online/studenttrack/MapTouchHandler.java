package xchg.online.studenttrack;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import org.xchg.online.baseframe.utils.Logger;

/**
 * Created by rsankarx on 16/01/17.
 */

public class MapTouchHandler extends FrameLayout {
    TrackLocationActivity activity;

    public MapTouchHandler(Context context) {
        super(context);
        Logger.d(MapTouchHandler.class.getSimpleName(), "Context is: " + context);
        activity = (TrackLocationActivity)context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                activity.toggleRecord();
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
