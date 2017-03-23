package xchg.online.studenttrack;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xchg.online.baseframe.fragment.LoginParentActivity;
import org.xchg.online.baseframe.listeners.LoginListener;
import org.xchg.online.baseframe.listeners.ProgressListener;
import org.xchg.online.baseframe.utils.Utilities;

import java.util.Map;

import xchg.online.studenttrack.smart.trackflow.LookupEvent;
import xchg.online.studenttrack.smart.trackflow.VerifyDriver;
import xchg.online.studenttrack.smart.trackflow.VerifyParent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OTPFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OTPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OTPFragment extends Fragment implements View.OnClickListener {

    private LoginActivity mParentActivity;
    private LoginListener mActListener;
    private ProgressListener mPDialogListener;
    private EditText mPhone;
    private EditText mOTP;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mName;

    public OTPFragment() {
        // Required empty public constructor
    }


    public static OTPFragment newInstance(Bundle bundle) {
        OTPFragment fragment = new OTPFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_otp, container, false);

        Button registerButton = (Button) rootView.findViewById(R.id.verify_button);
        registerButton.setOnClickListener(this);

        mOTP = (EditText) rootView.findViewById(R.id.verifycode);
        mPassword = (EditText) rootView.findViewById(R.id.password);
        mConfirmPassword = (EditText) rootView.findViewById(R.id.confirmpassword);
        mPhone = (EditText) rootView.findViewById(R.id.phone);
        mName = (EditText) rootView.findViewById(R.id.name);

        TextView mLogin = (TextView) rootView.findViewById(R.id.login);
        mLogin.setOnClickListener(this);

        TextView mRequest = (TextView) rootView.findViewById(R.id.requestOTP);
        mRequest.setOnClickListener(this);

        return rootView;
    }

    public void verifyUser() {
        if (TextUtils.isEmpty(mPhone.getText())) {
            mPhone.setError("Please enter phone Number");
        } else if (TextUtils.isEmpty(mOTP.getText())) {
            mOTP.setError("Please enter the OTP");
        } else if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.setError("Please select a password");
        } else if (TextUtils.isEmpty(mConfirmPassword.getText())) {
            mConfirmPassword.setError("Please confirm the password");
        } else if (!mConfirmPassword.getText().toString().equals(mPassword.getText().toString())) {
            mConfirmPassword.setError("Passwords do not match. Please enter same password.");
        } else if (TextUtils.isEmpty(mName.getText())) {
            mName.setError("Please enter the name.");
        } else {


            mPDialogListener.onProgress();
            String phone = mPhone.getText().toString();

            VerifyAndCreateTask task = new VerifyAndCreateTask(phone);
            task.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == org.xchg.online.baseframe.R.id.login) {
            mActListener.showLogin();
        } else if (view.getId() == org.xchg.online.baseframe.R.id.register) {
            mActListener.showRegister();
        } else if (view.getId() == R.id.requestOTP) {
            mParentActivity.showRequestOTP();
        } else if (view.getId() == org.xchg.online.baseframe.R.id.verify_button) {
            if (Utilities.isNetworkAvailable(mParentActivity)) {
                verifyUser();
            } else {
                Utilities.showToast(mParentActivity, getString(org.xchg.online.baseframe.R.string.check_internet_connection));
            }
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if ((activity instanceof Activity) && (activity instanceof LoginParentActivity)) {
            mParentActivity = (LoginActivity) getActivity();
            mActListener = ((LoginParentActivity)mParentActivity).loginListener();
            mPDialogListener = ((LoginParentActivity)mParentActivity).progressListener();
        }else {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginParentActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onSuccess() {
        mPDialogListener.onStopProgress();
        mActListener.onVerifySuccess();
    }

    public void onError(String msg) {
        mPDialogListener.onStopProgress();
        mActListener.onError();
    }


    public void verifyAndCreateDriver() {
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        String code = mOTP.getText().toString();

        VerifyDriver verify = new VerifyDriver(phone, code, password);
        verify.postTo(mParentActivity, new OnVerifiedDriverListener());
    }

    public void verifyAndCreateParent() {
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        String code = mOTP.getText().toString();
        String name = mName.getText().toString();

        VerifyParent verify = new VerifyParent(phone, name, code, password);
        verify.postTo(mParentActivity, new OnVerifiedParentListener());
    }

    public class OnVerifiedDriverListener implements VerifyDriver.VerifiedDriver {

        @Override
        public void onSuccess() {
            mPDialogListener.onStopProgress();
            Utilities.navigateToNextActivity(mParentActivity, LoginActivity.class, false);
            Toast.makeText(mParentActivity, "Verified. Please login.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(String msg) {
            mPDialogListener.onStopProgress();
            Toast.makeText(mParentActivity, msg, Toast.LENGTH_LONG).show();
        }
    }

    public class OnVerifiedParentListener implements VerifyParent.VerifiedParent {

        @Override
        public void onSuccess() {
            mPDialogListener.onStopProgress();
            Utilities.navigateToNextActivity(mParentActivity, LoginActivity.class, false);
            Toast.makeText(mParentActivity, "Verified. Please login.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(String msg) {
            mPDialogListener.onStopProgress();
            Toast.makeText(mParentActivity, msg, Toast.LENGTH_LONG).show();
        }
    }

    public class OnLookupListener implements LookupEvent.LookupDataListener {

        @Override
        public void onData(Map data) {
            verifyAndCreateDriver();
        }

        @Override
        public void noData() {
            verifyAndCreateParent();
        }

        @Override
        public void onError(String msg) {
            mPDialogListener.onStopProgress();
            Toast.makeText(mParentActivity, msg, Toast.LENGTH_LONG).show();
        }
    }

    public class VerifyAndCreateTask extends AsyncTask<Void, Void, Void> {

        private String phone;

        VerifyAndCreateTask(String ph) {
            phone = ph;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LookupEvent lookupEvent = new LookupEvent("Driver", phone);
            lookupEvent.postTo(mParentActivity, new OnLookupListener());
            return null;
        }
    }
}
