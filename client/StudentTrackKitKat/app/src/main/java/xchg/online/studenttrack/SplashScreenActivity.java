package xchg.online.studenttrack;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.anon.smart.client.SmartCommunicator;
import org.xchg.online.baseframe.activity.BaseActivity;
import org.xchg.online.baseframe.utils.AppPermissionConstants;
import org.xchg.online.baseframe.utils.Logger;
import org.xchg.online.baseframe.utils.Utilities;

public class SplashScreenActivity extends BaseActivity {
    private static final long LOCATION_HANDLER_TIME_OUT = 30000;
    private String TAG = SplashScreenActivity.class.getSimpleName();

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private static final int REQUEST_ID_GPS_SETTINGS = 3191;
    private static final int LOCATION_REQUEST_NO_OF_UPDATE = 1;

    private static final int SPLASH_TIME_OUT = 1000;

    private AlertDialog mDialog;
    private TextView mAnnotationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()");
        SmartCommunicator.getInstance(this).setSmartServer("securetrip.xchg.online", 9081, "kidtravel");
        //SmartCommunicator.getInstance(this).setSmartServer("139.59.6.59", 9081, "kidtravel");
        //SmartCommunicator.getInstance(this).setSmartServer("192.168.1.36", 9081, "kidtravel");
        setContentView(R.layout.activity_splash_screen);
        mAnnotationText = (TextView) findViewById(R.id.annotation_text);
        //handling onresume when the activity is restored from app icon
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            launchHomeScreen();
            finish();
        } else {

            Utilities.checkLocationPermission(this);
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launchHomeScreen();
                finish();
            }
        }, SPLASH_TIME_OUT);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppPermissionConstants.PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchHomeScreen();
                    finish();
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Logger.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume()");
        showHideAnnotation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void displayNoInternetAlert(String msg) {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(SplashScreenActivity.this);

        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                                SplashScreenActivity.this.finish();
                            }
                        })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    protected void handleLocationChange() {
        launchHomeScreen();
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected String getScreenName() {
        return SplashScreenActivity.class.getSimpleName();
    }


    private int getVersioncode() {
        PackageManager manager = getPackageManager();
        PackageInfo info;
        int versionCode = Integer.MAX_VALUE;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    public void showHideAnnotation() {
        /*if (BuildConfig.SHOW_VARIANT) {
            mAnnotationText.setVisibility(View.VISIBLE);
            mAnnotationText.setText(BuildConfig.FLAVOR + " " + BuildConfig.BUILD_TYPE + " " + BuildConfig.VERSION_NAME);
        } else {
            mAnnotationText.setVisibility(View.GONE);
        }*/
    }}
