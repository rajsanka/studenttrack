package org.xchg.online.baseframe.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;
*/


import org.xchg.online.baseframe.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rsankarx on 15/10/16.
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();
    private static Pattern pattern;
    private static Matcher matcher;

    //Mobile Pattern
    private static final String PHONE_PATTERN="^\\d{10}$";

    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Toast toast;

    /**
     * Validate Email with regular expression
     *
     * @param
     * @return true for Valid Email and false for Invalid Email
     */


    public static Toast cancellableToast(String msg, Context context) {
        if(toast != null)
            toast.cancel();
        toast=new Toast(context);
        LayoutInflater inflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastView = inflater.inflate(R.layout.toast_layout, null);
        LinearLayout linearlayouttoast = (LinearLayout) toastView.findViewById(R.id.linearlayout_toast);
        linearlayouttoast.getBackground().setAlpha(230);
        TextView toastTv = (TextView) toastView.findViewById(R.id.toast_text);
        toastTv.setText(msg);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    public static boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean validatePhone(String phone) {

        return phone.matches(PHONE_PATTERN);
    }

    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt) {
        return (txt != null && txt.trim().length() > 0 && !("null".equals(txt)));
    }

    public static float convertDpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }



    public static void showToast(Context context, String str) {
        if (context != null) {
            if(toast != null)
                toast.cancel();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //   Inflate the Layout
            View layout = inflater.inflate(R.layout.toast_layout, null);
            TextView toasttext = (TextView) layout.findViewById(R.id.toast_text);
            LinearLayout linearlayouttoast = (LinearLayout) layout.findViewById(R.id.linearlayout_toast);
            linearlayouttoast.getBackground().setAlpha(230);
            toasttext.setText(str);
            toast = new Toast(context);
            toast.setView(layout);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
            toast.show();
        }
    }

    public static void showTestingToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void showLog(String id, String msg) {
        Logger.d(id, msg);
    }


    public static boolean isNetworkAvailable(Activity context) {
        //checkNetworkPermission(context);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getIconStringResourceID(Context context, String icon_name) {
        return context.getResources().getIdentifier(BaseFrameConstants.ICON_ + icon_name, "string", BaseFrameConstants.PACKAGE_NAME);
    }

    public static <T> List<T> copyList(List<T> destination, List<T> source) {
        destination.clear();
        for (int i = 0; i < source.size(); i++) {
            destination.add(source.get(i));
        }
        return destination;
    }

    public static Bitmap createBitmapFromView(Context context, View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(metrics.widthPixels, metrics.heightPixels);
        view.layout(0, 0, metrics.widthPixels, metrics.heightPixels);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static String currencyFormat(double currency) {
        String currencyOut;
//        NumberFormat currencyFormatter;
//        String appenedRupeeAmount;
//
//        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
//        currencyFormatter.setMinimumFractionDigits(0);
//        currencyOut = currencyFormatter.format(currency);

        DecimalFormat df = new DecimalFormat("##,##,##,###.##");
        currencyOut = df.format(currency);


        return currencyOut;
    }


    public static int getScreenWidth(Context mContext){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Context mContext){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
/*
    public static void tryEnableLocation(LocationRequest mLocationRequest, GoogleApiClient mGoogleApiClient, ResultCallback<LocationSettingsResult> resultCallback) {
        Logger.d(TAG, "tryEnableLocation(" + mLocationRequest + ", " + mGoogleApiClient + ", " + resultCallback);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(resultCallback);
    }
    */

    // Function to capitalize each world or input string.
    // Eg. ThIs IS text. new ---> This Is Text. New
    public static String capitalizeEachWord(String str) {
        Log.d(TAG, "capitalizeEachWord(" + str + ")");
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        str = str.toLowerCase();
        StringBuilder stringBuilder = new StringBuilder(strLen);
        boolean isCapitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);


            if (Character.isWhitespace(ch)) {
                stringBuilder.append(ch);
                isCapitalizeNext = true;
            } else if (isCapitalizeNext) {
                stringBuilder.append(Character.toUpperCase(ch));
                isCapitalizeNext = false;
            } else {
                stringBuilder.append(ch);
            }
        }

        return stringBuilder.toString();
    }

    public static void hideSoftKeyboard(Activity mActivity) {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void checkLocationPermission(Activity activity) {
        checkAndGetPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION, AppPermissionConstants.PERMISSIONS_REQUEST_FINE_LOCATION);

    }

    public static void checkNetworkPermission(Activity activity) {
        checkAndGetPermissions(activity, Manifest.permission.ACCESS_NETWORK_STATE, AppPermissionConstants.PERMISSIONS_REQUEST_READ_PHONE_STATE);
    }

    public static void checkAndGetPermissions(Activity context, String permission, int request) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showRationalDialog(context);

            } else {
                ActivityCompat.requestPermissions(context, new String[]{ permission }, request);
            }
        }
    }

    public static void showRationalDialog(final Activity context) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(context);

        AlertDialog mDialog = null;

        builder.setMessage(R.string.splash_location_request_rationale)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.dismiss();
                        ActivityCompat.requestPermissions(context,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                AppPermissionConstants.PERMISSIONS_REQUEST_COARSE_LOCATION);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String msg = context.getString(R.string.splash_city_not_found_msg);
                        cancellableToast(msg, context);
                        //putDefaultCity();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    public static void navigateToNextActivity(Activity context, Class nextactivity, boolean checklogin) {
        Intent intent = new Intent(context, nextactivity);
        if (!checklogin || SessionManager.isLoggedIn(context)) {
            context.startActivity(intent);
            context.finish();
            context.overridePendingTransition(R.anim.slide_right_to_mid, R.anim.slide_mid_to_left);
        } else{
            Utilities.showToast(context, context.getString(R.string.login_to_continue));
        }
    }

    public static String convertTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dt = new Date(time); //time is assumed to be in millis seconds
        return format.format(dt);
    }

    public static String convertDuration(long insecs) {
        long mins = insecs / 60;
        long hrs = mins / 60;

        mins -= (hrs * 60);
        insecs -= ((hrs * 60 * 60) + (mins * 60));

        return hrs + " hrs " + mins + " mins " + insecs + " secs";
    }

    public static String convertDistance(double inmeters) {
        long kms = (long) (inmeters / 1000);
        long mtrs = (long) (inmeters - (kms * 1000));

        return kms + " kms " + mtrs + " m";
    }

    public static long getLong(Object obj) {
        long ret = 0;
        if (obj instanceof Integer) {
            ret = new Long((Integer) obj);
        } else if (obj instanceof Long) {
            ret = (long) obj;
        } else if (obj instanceof Double) {
            double change = (double) obj;
            ret = (long) change;
        }

        return ret;
    }

/*
    public static ArrayList<LatLng> decodePolyPoints(String encodedPath) {
        int len = encodedPath.length();

        final ArrayList<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }
    */
}
