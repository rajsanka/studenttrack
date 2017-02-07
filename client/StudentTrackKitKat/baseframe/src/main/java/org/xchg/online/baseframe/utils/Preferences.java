package org.xchg.online.baseframe.utils;

/**
 * Created by rsankarx on 15/10/16.
 */
public interface Preferences {
    public static final String CITY_PREFS_NAME = "prefsCity";
    public static final String CITY_JSON_KEY = "cityJsonKey";
    public static final String PLACE_PREFES_NAME = "PlaceIdPref";

    public static final String CLEAR_STORED_PREFS_NAME = "clearPrefs";
    //Login
    public static final String PREFS_LOGIN = "loggedin_user_id";
    public static final String KEY_LOGGED_IN_EMAIL = "loggedin_user_id_key";
    public static final String SESSION_ID = "session_id";
    public static final String PREFS_PROFILE = "loggedin_profile";
    public static final String KEY_LOGGED_IN_NAME = "loggedin_user_name";
    public static final String KEY_LOGGED_IN_PHONE = "loggedin_user_phone";

    public static final String DEVICE_DETAILS = "device_details";
    public static final String DEVICE_CREATED = "device_created";
    public static final String DEVICE_ID = "device_id";

    public static final String ALL_PERMITTED = "all_permitted";
    public static final String FEATURES_PERMITTED = "features_permitted";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String ROLE_NAME = "roleName";


    //Search last locality
    public static final String KEY_LOCALITY = "locality";
    public static final String KEY_LAT = "pLat";
    public static final String KEY_LNG = "pLng";
}
