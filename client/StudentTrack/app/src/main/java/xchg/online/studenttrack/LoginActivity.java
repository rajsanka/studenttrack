package xchg.online.studenttrack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.anon.smart.client.SmartResponseListener;
import org.anon.smart.client.SmartSecurity;
import org.xchg.online.baseframe.activity.BaseActivity;
import org.xchg.online.baseframe.fragment.LoginFragment;
import org.xchg.online.baseframe.fragment.LoginParentActivity;
import org.xchg.online.baseframe.listeners.LoginListener;
import org.xchg.online.baseframe.listeners.ProgressListener;
import org.xchg.online.baseframe.utils.SessionManager;
import org.xchg.online.baseframe.utils.Utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xchg.online.studenttrack.data.SessionData;
import xchg.online.studenttrack.data.TravelData;
import xchg.online.studenttrack.smart.trackflow.CreateDevice;
import xchg.online.studenttrack.smart.trackflow.LookupEvent;

public class LoginActivity extends BaseActivity implements LoginParentActivity, LoginListener, ProgressListener {

    public class ValidSessionCheck implements SmartSecurity.SessionCheckListener {

        private Activity parent;

        ValidSessionCheck(Activity activity) {
            parent = activity;
        }

        @Override
        public void handleValidSession() {
            dismissProgress();
            validSession = true;
            SessionManager.setupSmartCommunicator(parent);
            Utilities.navigateToNextActivity(parent, TrackLocationActivity.class, true);
        }

        @Override
        public void handleInvalidSession() {
            dismissProgress();
            Toast.makeText(parent, "Please Login again. Your session has expired.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void handleNetworkError(String s) {
            dismissProgress();
            Toast.makeText(parent, s, Toast.LENGTH_LONG).show();
        }
    }

    private LoginFragment mLoginFragment;
    //private RegisterFragment mRegisterFragment;
    //private VerifyFragment mVerifyFragment;
    private OTPFragment mOTPFragment;
    private String message;
    private boolean validSession;

    public String getScreenName() { return "Login"; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        invalidateOptionsMenu();

        if (SessionManager.isLoggedIn(this)) {
            //had already logged in. Hence use the sessid from there.
            //Have to modify for expiry.
            //SessionManager.setupSmartCommunicator(this);
            //Utilities.navigateToNextActivity(this, ProductListActivity.class);
            //go to next activity
            //return;
            boolean wait = SessionManager.checkValidSession(this, new ValidSessionCheck(this));
            if (wait) {
                showProgress("Validating session. Please Wait.");
                if (validSession) return;
            }
        }

        if (mLoginFragment == null) mLoginFragment = MyLoginFragment.newInstance(null);
        if (mOTPFragment == null) mOTPFragment = OTPFragment.newInstance(null);

        changeView(mLoginFragment);
        message = "Logging in.. Please wait..";

    }

    private void changeView(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.login_signup_fragment);
        if ((frag == null) || (frag.getId() != fragment.getId())) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.login_signup_fragment, fragment);
            transaction.commit();
        }
    }

    public LoginListener loginListener(){
        return this;
    }

    public ProgressListener progressListener(){
        return this;
    }

    public void createDevice() {
        if (SessionManager.isDeviceCreated(this)) {
            String devId = SessionManager.getDeviceId(this);
            TravelData.setDeviceId(devId);
            Utilities.navigateToNextActivity(this, TrackLocationActivity.class, true);
            return;
        }

        //Have to change this to get details from the manager.
        showProgress("Creating Device");
        //TelephonyManager mgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String devId = SessionManager.getLoggedInUser(this);;
        CreateDevice create = new CreateDevice();
        create.setDeviceId(devId);
        create.setPhoneNumber(SessionManager.getProfileEmail(this));
        create.setCountry("India");
        create.setRoaming(false);

        TravelData.setDeviceId(devId);
        create.postTo(this, new DeviceCreated(this, devId));
    }

    public void onLoginSuccess() {
        String role = SessionManager.getRoleName(this);
        SessionData sessdata = new SessionData(role);
        if (sessdata.isDriver()) {
            createDevice();
        } else {
            showProgress("Retriving Parent Information");
            LookupAndStoreParent lookupAndStoreParent = new LookupAndStoreParent(SessionManager.getLoggedInUser(this), this);
            lookupAndStoreParent.execute();
        }
        //Utilities.navigateToNextActivity(this, TrackLocationActivity.class);
    }

    public void onRegisterSuccess() {
        Toast.makeText(this, "Successfully Registered. Please check your email and enter your verification code.", Toast.LENGTH_LONG).show();

        showVerify();
    }

    public void onVerifySuccess() {
        Toast.makeText(this, "Successfully Verified. Please login to your account.", Toast.LENGTH_LONG).show();

        showLogin();
    }

    public void onError() {
        Toast.makeText(this, "Error during server access.", Toast.LENGTH_LONG).show();
    }

    public void onProgress() {
        showProgress(message);
    }

    public void onStopProgress() {
        dismissProgress();
    }

    public void showRegister() {
        changeView(mOTPFragment);
        message = "Verifying.. Please wait..";
    }

    public void showVerify() {
        //changeView(mVerifyFragment);
        //message = "Verifying.. Please wait..";
    }

    public void showLogin() {
        changeView(mLoginFragment);
        message = "Logging in.. Please wait..";
    }

    public static class DeviceCreated implements SmartResponseListener {

        private LoginActivity activity;
        private String deviceId;

        public DeviceCreated(LoginActivity parent, String devId) {
            activity = parent;
            deviceId = devId;
        }

        public void handleResponse(List responses){
            activity.onStopProgress();
            SessionManager.storeDevice(activity, deviceId);
            Utilities.navigateToNextActivity(activity, TrackLocationActivity.class, true);
        }

        public void handleError(double code, String context){
            activity.onStopProgress();
            Utilities.navigateToNextActivity(activity, TrackLocationActivity.class, true);
            Toast.makeText(activity, "Error in creating device", Toast.LENGTH_LONG).show();
        }

        public void handleNetworkError(String message){
            activity.onStopProgress();
            Utilities.navigateToNextActivity(activity, TrackLocationActivity.class, true);
            Toast.makeText(activity, "Error in creating device", Toast.LENGTH_LONG).show();
        }
    }

    public class OnLookupListener implements LookupEvent.LookupDataListener {

        LoginActivity parent;

        OnLookupListener(LoginActivity p) {
            parent = p;
        }

        @Override
        public void onData(Map data) {
            List lst = (List) data.get("trackDrivers");
            List<String> phones = new ArrayList<>();
            for (int i = 0; (lst != null) && (i < lst.size()); i++) {
                Map m = (Map) lst.get(i);
                String p = m.get("phone").toString();
                phones.add(p);
            }
            SessionData sessionData = new SessionData(SessionManager.getRoleName(parent));
            sessionData.setTrackNumbers(phones);
            sessionData.storeSessionData(parent);
            parent.onStopProgress();
            Utilities.navigateToNextActivity(parent, TrackLocationActivity.class, true);
        }

        @Override
        public void noData() {
            parent.onStopProgress();
            Toast.makeText(parent, "Cannot Find parent.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(String msg) {
            parent.onStopProgress();
            Toast.makeText(parent, msg, Toast.LENGTH_LONG).show();
        }
    }

    public class LookupAndStoreParent extends AsyncTask<Void, Void, Void> {

        private String phone;
        private LoginActivity parent;

        LookupAndStoreParent(String ph, LoginActivity p) {
            phone = ph;
            parent = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SessionData d = SessionData.fromStore(parent);
            if (d == null) {
                LookupEvent lookupEvent = new LookupEvent("Parent", phone);
                lookupEvent.postTo(parent, new OnLookupListener(parent));
            } else {
                parent.onStopProgress();
            }
            return null;
        }
    }

}
