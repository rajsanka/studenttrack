package org.xchg.online.baseframe.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.anon.smart.client.SmartResponseListener;
import org.anon.smart.client.SmartSecurity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xchg.online.baseframe.utils.BaseFrameConstants;
import org.xchg.online.baseframe.utils.Logger;
import org.xchg.online.baseframe.utils.Preferences;
import org.xchg.online.baseframe.R;
import org.xchg.online.baseframe.listeners.LoginListener;
import org.xchg.online.baseframe.listeners.ProgressListener;
import org.xchg.online.baseframe.utils.SessionManager;
import org.xchg.online.baseframe.utils.Utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import xchg.online.register.LookupEvent;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    public static class FeaturesRequestListener implements SmartSecurity.FeaturesListener {

        private Context _context;
        private LoginFragment _fragment;

        FeaturesRequestListener(Context ctx, LoginFragment fragment) {
            _context = ctx;
            _fragment = fragment;
        }

        @Override
        public void handleRoleName(String role) {
            SessionManager.storeRoleName(_context, role);
        }

        public void handleAllPermitted(){
            SessionManager.markAllPermitted(_context);
        }

        public void handleFeature(String feature, String permit) {
            SessionManager.storeFeaturePermitted(_context, feature, permit);
        }

        public void handleResponse(List responses) {
            _fragment.onLoginSuccess();
            _fragment.mPDialogListener.onStopProgress();
        }

        public void handleError(double code, String context) {
            _fragment.mPDialogListener.onStopProgress();
            Toast.makeText(_context, "Error reading user details. Please logout and login again.", Toast.LENGTH_LONG).show();
        }

        public void handleNetworkError(String message) {
            _fragment.mPDialogListener.onStopProgress();
            Toast.makeText(_context, "A network error has occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    public static class ProfileListener implements LookupEvent.LookupProfileListener {

        private Context _context;

        ProfileListener(Context ctx) {
            _context = ctx;
        }

        @Override
        public void onProfile(Map data) {

            if (data == null) return;

            String email = data.get("email").toString();
            String name = data.get("name").toString();
            String phone = data.get("phone").toString();

            SessionManager.storeProfile(_context, email, name, phone);
        }

        @Override
        public void onError(String msg) {

        }
    }

    public static class LoginRequestListener implements SmartResponseListener {
        private Context _context;
        private LoginFragment _fragment;

        LoginRequestListener(Context ctx, LoginFragment fragment) {
            _context = ctx;
            _fragment = fragment;
        }

        public void handleResponse(List responses) {
            SessionManager.storeSession(_context, _fragment.mUserEmail, SmartSecurity.getLastSessionId());
            SmartSecurity.getPermittedFeatures((Activity) _context, new FeaturesRequestListener(_context, _fragment));

            if (_fragment.readProfile()) {
                LookupEvent event = new LookupEvent(_fragment.mUserEmail);
                event.postTo((Activity) _context, new ProfileListener(_context));
            }
        }

        public void handleError(double code, String context) {
            _fragment.mPDialogListener.onStopProgress();
            Toast.makeText(_context, "Invalid phone or password. Please login again.", Toast.LENGTH_LONG).show();
        }

        public void handleNetworkError(String message) {
            _fragment.mPDialogListener.onStopProgress();
            Toast.makeText(_context, "A network error has occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public boolean readProfile() {
        return true;
    }

    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private Activity mParentActivity;
    private EditText mUserEmailTv;
    private EditText mUserPasswordTv;
    private Dialog mForgetDialog;
    private String mUserEmail;
    private String mUserPassword;
    private LoginListener mActListener;
    protected static LoginFragment mLoginFragment;
    private String mRegid;
    private ProgressListener mPDialogListener;
    private boolean mIsResolving = false;
    private boolean mIsInputChanged = false;

    protected LoginRequestListener _requestListener;


    public static LoginFragment newInstance(Bundle bundle) {
        mLoginFragment = new LoginFragment();
        mLoginFragment.setArguments(bundle);
        return mLoginFragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if ((activity instanceof Activity) && (activity instanceof LoginParentActivity)) {
            mParentActivity = (Activity) getActivity();
            mActListener = ((LoginParentActivity)mParentActivity).loginListener();
            mPDialogListener = ((LoginParentActivity)mParentActivity).progressListener();
            _requestListener = new LoginRequestListener(mParentActivity, this);
        }else {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginParentActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        //showHashKey(mParentActivity);

        Button mLoginBtn = (Button) rootView.findViewById(R.id.login_button);
        mLoginBtn.setOnClickListener(this);

        mUserEmailTv = (EditText) rootView.findViewById(R.id.user_name);
        mUserPasswordTv = (EditText) rootView.findViewById(R.id.password);
        mUserPasswordTv.setOnTouchListener(new PasswordOnTouchListener());
        mPasswordDrawable = R.drawable.show_password;
        mEmailDrawable = R.drawable.ic_close_black;
        mUserPasswordTv.addTextChangedListener(mPasswordWatcher);
        TextView mForgetPasswordTv = (TextView) rootView.findViewById(R.id.forgot_password);
        mForgetPasswordTv.setOnClickListener(this);
        TextView mRegister = (TextView) rootView.findViewById(R.id.register);
        mRegister.setOnClickListener(this);

        SharedPreferences prefs = mParentActivity.getSharedPreferences(BaseFrameConstants.DEVICE_REG_ID_PREF, Context.MODE_PRIVATE);
        mRegid = prefs.getString(BaseFrameConstants.DEVICE_REG_ID_PREF_KEY, null);


        SharedPreferences prefs_loggedinuser = mParentActivity.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        String email = prefs_loggedinuser.getString(Preferences.KEY_LOGGED_IN_EMAIL, "");
        mUserEmailTv.setText(email);
        mUserEmailTv.setSelection(email.length());
        if(!TextUtils.isEmpty(email)){
            // setting focus to password text view
            mUserPasswordTv.requestFocus();
            mUserEmailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, mEmailDrawable, 0);
        }
        mUserEmailTv.addTextChangedListener(mEamilWatcher);
        mUserEmailTv.setOnTouchListener(new EmailOnTouchListener());
        return rootView;
    }

    private void authenticateUser() {
        hideKeyboard();
        mUserEmail = mUserEmailTv.getText().toString();
        mUserPassword = mUserPasswordTv.getText().toString();

        //gcm commented
        SharedPreferences prefs = mParentActivity.getSharedPreferences(BaseFrameConstants.DEVICE_REG_ID_PREF, Context.MODE_PRIVATE);
        mRegid = prefs.getString(BaseFrameConstants.DEVICE_REG_ID_PREF_KEY, null);

        if (TextUtils.isEmpty(mUserEmail)) {
            mUserEmailTv.requestFocus();
            mUserEmailTv.setError(getResources().getString(R.string.error_email));
        /*} else if (!Utilities.validateEmail(mUserEmail)) {
            mUserEmailTv.requestFocus();
            mUserEmailTv.setError("Please enter valid email address");*/
        } else if (TextUtils.isEmpty(mUserPassword)) {
            mUserPasswordTv.requestFocus();
            mUserPasswordTv.setError("Please enter password");
        } else {
            SmartSecurity.autheticate(mParentActivity, mUserEmail, mUserPassword, _requestListener);
            mPDialogListener.onProgress();
        }
    }

/*
    private void showVerifyDialog(String msg, String email) {
        LayoutInflater inflater = (LayoutInflater) mParentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View DialogView = inflater.inflate(R.layout.forgot_password_layout, null);

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogParams.setMargins(5, 10, 5, 10);
        mForgetDialog = new Dialog(mParentActivity);
        mForgetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mForgetDialog.setContentView(DialogView, dialogParams);
        mForgetDialog.show();

        final Button sendResetLink = (Button) DialogView.findViewById(R.id.send_reset_link_button);
        sendResetLink.setText(R.string.login_send_email_verification);
        final TextView emailTv = (TextView) DialogView.findViewById(R.id.email);
        emailTv.setText(email);
        final TextView hintTv = (TextView) DialogView.findViewById(R.id.hint);
        hintTv.setText(msg);
        sendResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                sendEmailVerification(emailTv);
            }
        });

    }*/

    /*
    private void sendEmailVerification(TextView emailTv) {

        String email = emailTv.getText().toString();
        if (Utilities.validateEmail(email)) {

            mPDialogListener.onProgressDialogShow();
            JSONObject verifyData = new JSONObject();
            try {
                verifyData.put("user", new JSONObject().put("email", email));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest verifyTask = new JsonObjectRequest(
                    Request.Method.POST,
                    RequestURL.VERIFY_EMAIL,
                    verifyData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject object) {
                            //progressDialog.hide();
                            mPDialogListener.onProgressDialogHide();
                            mForgetDialog.dismiss();
                            Utilities.showToast(mParentActivity, "Email verification instructions has been sent to your email address");
                        }
                    },
                    new ErrorResponseListener(mParentActivity) {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //progressDialog.hide();
                            mPDialogListener.onProgressDialogHide();
                            if (volleyError.networkResponse != null) {
                                if (volleyError.networkResponse.statusCode == 422) {
                                    Utilities.showToast(mParentActivity, getString(R.string.error_response_invalid_email));
                                    return;
                                }
                            }
                            super.onErrorResponse(volleyError);
                        }
                    }
            );
            AppController.getInstance().setRetryPolicy(verifyTask);
            AppController.getInstance().addToRequestQueue(verifyTask);
        } else {
            emailTv.setError(mParentActivity.getResources().getString(R.string.error_invalid_email));
        }
    }
*/
/*
    private void createPasswordResetPopup() {
        LayoutInflater inflater = (LayoutInflater) mParentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View DialogView = inflater.inflate(R.layout.forgot_password_layout, null);

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogParams.setMargins(5, 10, 5, 10);
        mForgetDialog = new Dialog(mParentActivity);
        mForgetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mForgetDialog.setContentView(DialogView, dialogParams);
        mForgetDialog.show();

        final Button sendResetLink = (Button) DialogView.findViewById(R.id.send_reset_link_button);
        final TextView emailTv = (TextView) DialogView.findViewById(R.id.email);
        sendResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                sendResetInstruction(emailTv);
            }
        });
    }
    */

    public boolean isInputExists() {
        String userMail = mUserEmailTv.getText().toString();
        String password = mUserPasswordTv.getText().toString();
        userMail = mIsInputChanged ? userMail : "";
        return isNotNull(userMail, password);
    }

    private boolean isNotNull(String... texts) {
        for (String txt : texts) {
            if (txt != null && txt.trim().length() > 0) {
                return true;
            }
        }
        return false;
    }

    private void hideKeyboard() {
        View view = mParentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /*
    private void sendResetInstruction(TextView emailTv) {
        String email = emailTv.getText().toString();
        if (Utilities.validateEmail(email)) {

            mPDialogListener.onProgressDialogShow();
            JSONObject resetData = new JSONObject();
            try {
                resetData.put("user", new JSONObject().put("email", email));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest resetTask = new JsonObjectRequest(
                    Request.Method.POST,
                    RequestURL.PASSWORD_RESET_URL,
                    resetData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject object) {
                            //progressDialog.hide();
                            mPDialogListener.onProgressDialogHide();
                            mForgetDialog.dismiss();
                            Utilities.showToast(mParentActivity, "Reset instruction has been sent to your email address");
                        }
                    },
                    new ErrorResponseListener(mParentActivity) {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //progressDialog.hide();
                            mPDialogListener.onProgressDialogHide();
                            if (volleyError.networkResponse != null) {
                                if (volleyError.networkResponse.statusCode == 422) {
                                    Utilities.showToast(mParentActivity, getString(R.string.login_email_not_found));
                                } else {
                                    Utilities.showToast(mParentActivity, "Something went wrong! Try again later");
                                }
                            } else {
                                Utilities.showToast(mParentActivity, "No internet connection");
                            }
                        }
                    }
            );

            AppController.getInstance().setRetryPolicy(resetTask);
            AppController.getInstance().addToRequestQueue(resetTask);

        } else {
            emailTv.setError(mParentActivity.getResources().getString(R.string.error_invalid_email));
        }
    }*/

    private int mPasswordDrawable = 0;
    private final TextWatcher mPasswordWatcher = new TextWatcher() {
        private boolean isDrawableUpdateRequired = true;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                mUserPasswordTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                isDrawableUpdateRequired = true;
            } else if (isDrawableUpdateRequired) {
                isDrawableUpdateRequired = false;
                mUserPasswordTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, mPasswordDrawable, 0);
            }
        }
    };
    private int mEmailDrawable = 0;
    private final TextWatcher mEamilWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            mIsInputChanged = true;
            if (s.length() == 0) {
                mUserEmailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                mUserEmailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, mEmailDrawable, 0);
            }
        }
    };

    private class PasswordOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            if ((view instanceof EditText) && event.getAction() == MotionEvent.ACTION_UP) {
                EditText editText = (EditText) view;
                Drawable showDrawable = ((TextView) view).getCompoundDrawables()[2];
                if ((showDrawable != null) && (event.getX() > view.getMeasuredWidth() - view.getPaddingRight() - showDrawable.getIntrinsicWidth())) {
                    if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        int cursorPortion = editText.getSelectionEnd();
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        mPasswordDrawable = R.drawable.hide_password;
                        editText.setSelection(cursorPortion);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_password, 0);
                    } else {
                        int cursorPortion = editText.getSelectionStart();
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPasswordDrawable = R.drawable.show_password;
                        editText.setSelection(cursorPortion);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0);
                    }
                }
            }
            return false;
        }
    }

    private class EmailOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            if ((view instanceof EditText) && event.getAction() == MotionEvent.ACTION_UP) {
                Drawable showDrawable = ((TextView) view).getCompoundDrawables()[2];
                if ((showDrawable != null) && (event.getX() > view.getMeasuredWidth() - view.getPaddingRight() - showDrawable.getIntrinsicWidth())) {
                    mUserEmailTv.setText("");
                    SharedPreferences prefs_loggedinuser = mParentActivity.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
                    prefs_loggedinuser.edit().putString(Preferences.KEY_LOGGED_IN_EMAIL, "").apply();
                }
            }
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.forgot_password) {
            //createPasswordResetPopup();
        } else if (view.getId() == R.id.register) {
            if (mActListener != null) {
                mActListener.showRegister();
            }
        } else if (view.getId() == R.id.login_button) {
            if (Utilities.isNetworkAvailable(mParentActivity)) {
                authenticateUser();
            } else {
                Utilities.showToast(mParentActivity, getString(R.string.check_internet_connection));
            }
        }
    }

    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "org.xchg.online", PackageManager.GET_SIGNATURES); //Your package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Logger.i(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void onLoginSuccess() {
        if (mActListener != null) {
            mActListener.onLoginSuccess();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Logger.i(TAG, "onstart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.i(TAG, "onstop");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(mUserPasswordTv.getText())){
            mUserPasswordTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, mPasswordDrawable, 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String tag, Object data);
    }
}
