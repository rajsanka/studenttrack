package xchg.online.studenttrack;

import android.os.Bundle;

import org.xchg.online.baseframe.fragment.LoginFragment;

/**
 * Created by rsankarx on 17/01/17.
 */

public class MyLoginFragment extends LoginFragment {

    public static LoginFragment newInstance(Bundle bundle) {
        mLoginFragment = new MyLoginFragment();
        mLoginFragment.setArguments(bundle);
        return mLoginFragment;
    }

    @Override
    public boolean readProfile() {
        return false;
    }
}
