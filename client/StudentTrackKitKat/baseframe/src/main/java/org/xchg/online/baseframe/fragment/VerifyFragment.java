package org.xchg.online.baseframe.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xchg.online.baseframe.R;
import org.xchg.online.baseframe.listeners.LoginListener;
import org.xchg.online.baseframe.listeners.ProgressListener;
import org.xchg.online.baseframe.utils.Utilities;

import xchg.online.register.Verify;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerifyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyFragment extends Fragment implements View.OnClickListener, Verify.VerifyListener {

    private OnFragmentInteractionListener mListener;

    private Activity mParentActivity;
    private LoginListener mActListener;
    private ProgressListener mPDialogListener;
    private EditText mEmail;
    private EditText mVerifyCode;
    private EditText mPassword;
    private EditText mConfirmPassword;

    public VerifyFragment() {
        // Required empty public constructor
    }


    public static VerifyFragment newInstance(Bundle bundle) {
        VerifyFragment fragment = new VerifyFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_verify, container, false);

        Button registerButton = (Button) rootView.findViewById(R.id.verify_button);
        registerButton.setOnClickListener(this);

        mVerifyCode = (EditText) rootView.findViewById(R.id.verifycode);
        mPassword = (EditText) rootView.findViewById(R.id.password);
        mConfirmPassword = (EditText) rootView.findViewById(R.id.confirmpassword);
        mEmail = (EditText) rootView.findViewById(R.id.email);

        TextView mLogin = (TextView) rootView.findViewById(R.id.login);
        mLogin.setOnClickListener(this);

        TextView mRegister = (TextView) rootView.findViewById(R.id.register);
        mRegister.setOnClickListener(this);

        return rootView;
    }

    private void hideKeyboard() {
        View view = mParentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void verifyUser() {
        hideKeyboard();
        if (TextUtils.isEmpty(mEmail.getText())) {
            mEmail.requestFocus();
            mEmail.setError("Please enter email");
        } else if (!Utilities.validateEmail(mEmail.getText().toString())) {
            mEmail.requestFocus();
            mEmail.setError("Please enter valid email address");
        } else if (TextUtils.isEmpty(mVerifyCode.getText())) {
            mVerifyCode.requestFocus();
            mVerifyCode.setError("Please enter the code");
        } else if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.requestFocus();
            mPassword.setError("Please select a password");
        } else if (TextUtils.isEmpty(mConfirmPassword.getText())) {
            mConfirmPassword.requestFocus();
            mConfirmPassword.setError("Please confirm the password");
        } else if (!mConfirmPassword.getText().toString().equals(mPassword.getText().toString())) {
            mConfirmPassword.requestFocus();
            mConfirmPassword.setError("Passwords do not match. Please enter same password.");
        } else {
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            String code = mVerifyCode.getText().toString();

            Verify verify = new Verify(email);
            verify.setCode(code);
            verify.setPassword(password);

            mPDialogListener.onProgress();
            verify.postTo(mParentActivity, this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login) {
            mActListener.showLogin();
        } else if (view.getId() == R.id.register) {
            mActListener.showRegister();
        } else if (view.getId() == R.id.verify_button) {
            if (Utilities.isNetworkAvailable(mParentActivity)) {
                verifyUser();
            } else {
                Utilities.showToast(mParentActivity, getString(R.string.check_internet_connection));
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
        mListener = null;
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
        void onFragmentInteraction(String tag, Object data);
    }

    public void onSuccess() {
        mPDialogListener.onStopProgress();
        mActListener.onVerifySuccess();
    }

    public void onError(String msg) {
        mPDialogListener.onStopProgress();
        mActListener.onError();
    }
}
