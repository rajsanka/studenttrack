package org.xchg.online.baseframe.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import org.xchg.online.baseframe.R;
import org.xchg.online.baseframe.listeners.LoginListener;
import org.xchg.online.baseframe.listeners.ProgressListener;
import org.xchg.online.baseframe.utils.BaseFrameConstants;
import org.xchg.online.baseframe.utils.Preferences;
import org.xchg.online.baseframe.utils.Utilities;

import xchg.online.register.Register;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener, Register.RegisterListener {

    private OnFragmentInteractionListener mListener;

    private Activity mParentActivity;
    private LoginListener mActListener;
    private ProgressListener mPDialogListener;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mName;
    private String mRole;

    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(Bundle bundle, String role) {
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(bundle);
        fragment.mRole = role;
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
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        Button registerButton = (Button) rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        mEmail = (EditText) rootView.findViewById(R.id.email);
        mPhone = (EditText) rootView.findViewById(R.id.phone);
        mName = (EditText) rootView.findViewById(R.id.name);

        TextView mLogin = (TextView) rootView.findViewById(R.id.login);
        mLogin.setOnClickListener(this);

        TextView mverify = (TextView) rootView.findViewById(R.id.verify);
        mverify.setOnClickListener(this);

        return rootView;
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

    private void registerUser() {
        hideKeyboard();
        if (TextUtils.isEmpty(mEmail.getText())) {
            mEmail.requestFocus();
            mEmail.setError("Please enter email");
        } else if (!Utilities.validateEmail(mEmail.getText().toString())) {
            mEmail.requestFocus();
            mEmail.setError("Please enter valid email address");
        } else if (TextUtils.isEmpty(mName.getText())) {
            mName.requestFocus();
            mName.setError("Please enter name");
        } else if (TextUtils.isEmpty(mPhone.getText())) {
            mPhone.requestFocus();
            mPhone.setError("Please enter phone");
        } else {
            String email = mEmail.getText().toString();
            String name = mName.getText().toString();
            String phone = mPhone.getText().toString();

            Register register = new Register(mRole);
            register.setEmail(email);
            register.setName(name);
            register.setPhone(phone);
            register.setDefaultCity("Bangalore");

            mPDialogListener.onProgress();
            register.postTo(mParentActivity, this);
        }
    }

    private void hideKeyboard() {
        View view = mParentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login) {
            mActListener.showLogin();
        } else if (view.getId() == R.id.verify) {
            mActListener.showVerify();
        } else if (view.getId() == R.id.register_button) {
            if (Utilities.isNetworkAvailable(mParentActivity)) {
                registerUser();
            } else {
                Utilities.showToast(mParentActivity, getString(R.string.check_internet_connection));
            }
        }
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

    public void onSuccess(String email) {
        mPDialogListener.onStopProgress();
        //need to switch to verify
        mActListener.onRegisterSuccess();
        //Toast.makeText(mParentActivity, "Successfully Registered. Please check your email and enter your verification code.", Toast.LENGTH_LONG).show();
    }

    public void onError(String msg) {
        mPDialogListener.onStopProgress();
        //Toast.makeText(mParentActivity, msg, Toast.LENGTH_LONG).show();
        mActListener.onError();
    }
}
