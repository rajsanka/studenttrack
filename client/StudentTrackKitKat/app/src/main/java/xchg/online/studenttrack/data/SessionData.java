package xchg.online.studenttrack.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xchg.online.studenttrack.smart.trackflow.GetCurrentTripLocation;

/**
 * Created by rsankarx on 17/01/17.
 */

public class SessionData {
    private static final String DRIVER = "driver";
    private static final String PARENT = "parent";

    private static final String SESSION_DATA = "session_data";
    private static final String SESSION_ROLE = "session_role";
    private static final String SESSION_NUMBERS = "session_numbers";

    private String role;
    private Set<String> trackNumbers;

    public SessionData(String r) {
        role = r;
    }

    public void setTrackNumbers(List<String> numbers) {
        trackNumbers = new HashSet<>();
        trackNumbers.addAll(numbers);
    }

    public boolean isDriver() {
        return role.equals(DRIVER);
    }

    public boolean isParent() {
        return role.equals(PARENT);
    }

    public Set<String> getTrackNumbers() { return trackNumbers; }

    public void storeSessionData(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(SESSION_DATA, 0);
        preferences.edit().putString(SESSION_ROLE, role).apply();
        preferences.edit().putStringSet(SESSION_NUMBERS, trackNumbers).apply();
    }

    public static SessionData fromStore(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(SESSION_DATA, 0);
        String role = preferences.getString(SESSION_ROLE, null);
        Set<String> numbers = preferences.getStringSet(SESSION_NUMBERS, null);
        if (role != null) {
            SessionData ret = new SessionData(role);
            ret.trackNumbers = numbers;
            return ret;
        }

        return  null;
    }

    public static boolean isDriver(String r) {
        return r.equals(DRIVER);
    }

    public static void reset(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(SESSION_DATA, 0);
        preferences.edit().remove(SESSION_ROLE).apply();
        preferences.edit().remove(SESSION_NUMBERS).apply();
    }
}
