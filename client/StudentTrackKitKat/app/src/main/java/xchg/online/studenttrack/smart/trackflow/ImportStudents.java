package xchg.online.studenttrack.smart.trackflow;

import android.app.Activity;
import android.util.Log;

import org.anon.smart.client.SmartEvent;
import org.anon.smart.client.SmartResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsankarx on 22/02/17.
 */

public class ImportStudents extends SmartEvent {
    public interface ImportedStudents {
        public void onSuccess();
        public void onError(String msg);
    }

    public class ImportStudentsSmartListener implements SmartResponseListener {

        ImportedStudents listener;

        ImportStudentsSmartListener(ImportedStudents l) {
            listener = l;
        }

        @Override
        public void handleResponse(List list) {
            listener.onSuccess();
        }

        @Override
        public void handleError(double code, String context) {
            String message = code + ":" + context;
            Log.i(TAG, "Error Searching data: " + message);
            listener.onError(message);
        }

        @Override
        public void handleNetworkError(String message) {
            Log.i(TAG, "Error Searching data: " + message);
            listener.onError(message);
        }
    }

    private static final String TAG = LookupEvent.class.getSimpleName();
    private static final String FLOW = "StudentFlow";
    private String driverPhone;
    private String parentPhone;
    private String parentName;
    private String studentName;

    public ImportStudents(String dph, String pph, String pnm, String snm) {
        super(FLOW);
        driverPhone = dph;
        parentPhone = pph;
        parentName = pnm;
        studentName = snm;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> parms = new HashMap<>();
        List<Map> students = new ArrayList<>();
        Map<String, Object> onestudent = new HashMap<>();

        onestudent.put("name", studentName);
        onestudent.put("parentPhone", parentPhone);
        onestudent.put("parentName", parentName);
        students.add(onestudent);

        parms.put("students", students);

        return parms;
    }

    public void postTo(Activity activity, ImportedStudents listener) {
        super.postEvent(activity, new ImportStudentsSmartListener(listener), "Driver", driverPhone);
    }
}
