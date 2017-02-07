package xchg.online.studenttrack.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xchg.online.baseframe.utils.Logger;

import java.util.List;
import java.util.Locale;

import xchg.online.studenttrack.ErrorDialogFragment;
import xchg.online.studenttrack.R;
import xchg.online.studenttrack.data.TripSummary;

/**
 * Created by rsankarx on 16/01/17.
 */

public class MapUtils {
    public static void putDefaultCity() {
        //SharedPreferences.Editor editor = prefsCity.edit();
        //editor.putString(IntentParseKeys.CITY, CityBoundsAndLimit.BANGALORE);
        //editor.apply();
        //CityBoundsAndLimit.putCurrentCity(SplashScreenActivity.this, CityBoundsAndLimit.BANGALORE);
    }

    public static void cancellableToast(Activity activity, Toast mToast, String msg) {
        View toastView = activity.getLayoutInflater().inflate(R.layout.toast_layout, null);
        LinearLayout linearLayoutToast = (LinearLayout) toastView.findViewById(R.id.linearlayout_toast);
        linearLayoutToast.getBackground().setAlpha(170);
        TextView toastTv = (TextView) toastView.findViewById(R.id.toast_text);
        toastTv.setText(msg);
        mToast.setView(toastView);
        mToast.show();
    }

    /* Creates a dialog for an error message */
    public static void showErrorDialog(Activity activity, FragmentManager mgr, int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(TrafficConstants.DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.setDismissedListener((OnErrorDialogDismissedListener) activity);
        if (!activity.isFinishing()) {
            dialogFragment.show(mgr, "errordialog");
        }
    }

    public static void showRecordButton(Activity activity, final boolean start, final OnRecordListener listener) {
        /*final Dialog dialog = new Dialog(activity, R.style.Theme_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.button_layout);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button button = (Button)dialog.findViewById(R.id.btnRecord);
        if (start) {
            button.setBackgroundResource(R.drawable.record_black);
        } else {
            button.setBackgroundResource(R.drawable.stop);
        }
        //button.getBackground().setAlpha(40);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecord(start);
                dialog.dismiss();
            }
        });

        Button cancel = (Button)dialog.findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancel(start);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();*/


    }

    public static void showSummary(Activity activity, final TripSummary summary, final OnTripSaveListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.trip_summary_layout);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);

        TextView txt = (TextView) dialog.findViewById(R.id.startTime);
        txt.setText(summary.getStartDate());

        txt = (TextView) dialog.findViewById(R.id.endTime);
        txt.setText(summary.getEndDate());

        //txt = (TextView) dialog.findViewById(R.id.distance);
        //txt.setText(summary.getDistanceTraveled());

        txt = (TextView) dialog.findViewById(R.id.duration);
        txt.setText(summary.getDurationTraveled());

        txt = (TextView) dialog.findViewById(R.id.origin);
        txt.setText(summary.getOrigin());

        txt = (TextView) dialog.findViewById(R.id.destination);
        txt.setText(summary.getDestination());

        txt = (TextView) dialog.findViewById(R.id.header);
        txt.setText("Trip Summary");

        /*Button button = (Button)dialog.findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSave("Test", summary);
                dialog.dismiss();
            }
        });*/

        Button cancel = (Button)dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancel(summary);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public static String getAddress(Activity activity, double latitude, double longitude) {
        String ret = "Unknown";
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(activity, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            ret = knownName + ":" + address + "," + city + "," + state + "," + postalCode + "," + country;

        } catch (Exception e) {
            Logger.e(MapUtils.class.getSimpleName(), "Exception in getting address: " + e.getMessage());
        }

        return ret;
    }

    public final static double AVERAGE_RADIUS_OF_EARTH = 6372.8;
    public static double calculateDistance(double lat1, double lng1,
                                           double lat2, double lng2) {

        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(lng1 - lng2);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return ((AVERAGE_RADIUS_OF_EARTH * c) * 1000); //distance in meters
    }

}
