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

import xchg.online.studenttrack.smart.trackflow.ImportStudents;


public class RequestOTPFragment extends Fragment implements View.OnClickListener {
    private Activity mParentActivity;
    private LoginListener mActListener;
    private ProgressListener mPDialogListener;
    private EditText driverPhone;
    private EditText parentPhone;
    private EditText parentName;
    private EditText studentName;

    public RequestOTPFragment() {
        // Required empty public constructor
    }


    public static RequestOTPFragment newInstance(Bundle bundle) {
        RequestOTPFragment fragment = new RequestOTPFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_request_otp, container, false);

        Button registerButton = (Button) rootView.findViewById(R.id.request_otp);
        registerButton.setOnClickListener(this);

        driverPhone = (EditText) rootView.findViewById(R.id.driverphone);
        parentPhone = (EditText) rootView.findViewById(R.id.parentphone);
        parentName = (EditText) rootView.findViewById(R.id.name);
        studentName = (EditText) rootView.findViewById(R.id.studentname);

        TextView mLogin = (TextView) rootView.findViewById(R.id.login);
        mLogin.setOnClickListener(this);

        return rootView;
    }

    public void requestOTP() {
        if (TextUtils.isEmpty(driverPhone.getText())) {
            driverPhone.setError("Please enter phone Number");
        } else if (TextUtils.isEmpty(parentPhone.getText())) {
            parentPhone.setError("Please enter your Phone");
        } else if (TextUtils.isEmpty(parentName.getText())) {
            parentName.setError("Please enter your Name");
        } else if (TextUtils.isEmpty(studentName.getText())) {
            studentName.setError("Please enter the name of the student");
        } else {


            mPDialogListener.onProgress();

            ImportStudentsTask task = new ImportStudentsTask();
            task.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == org.xchg.online.baseframe.R.id.login) {
            mActListener.showLogin();
        } else if (view.getId() == org.xchg.online.baseframe.R.id.register) {
            mActListener.showRegister();
        } else if (view.getId() == R.id.request_otp) {
            if (Utilities.isNetworkAvailable(mParentActivity)) {
                requestOTP();
            } else {
                Utilities.showToast(mParentActivity, getString(org.xchg.online.baseframe.R.string.check_internet_connection));
            }
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if ((activity instanceof Activity) && (activity instanceof LoginParentActivity)) {
            mParentActivity = (Activity) getActivity();
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


    public void importStudents() {
        String dphone = driverPhone.getText().toString();
        String pphone = parentPhone.getText().toString();
        String pname = parentName.getText().toString();
        String sname = studentName.getText().toString();

        ImportStudents importStudents = new ImportStudents(dphone, pphone, pname, sname);
        importStudents.postTo(mParentActivity, new OnImportedStudents());
    }


    public class OnImportedStudents implements ImportStudents.ImportedStudents {

        @Override
        public void onSuccess() {
            mPDialogListener.onStopProgress();
            //Utilities.navigateToNextActivity(mParentActivity, LoginActivity.class, false);
            mActListener.showLogin();
            Toast.makeText(mParentActivity, "OTP Request Sent. The information will be verified and OTP sent.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(String msg) {
            mPDialogListener.onStopProgress();
            Toast.makeText(mParentActivity, msg, Toast.LENGTH_LONG).show();
        }
    }


    public class ImportStudentsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            importStudents();
            return null;
        }
    }
}
