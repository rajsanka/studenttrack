package org.xchg.online.baseframe.fragment;

import android.app.Activity;

import org.xchg.online.baseframe.listeners.LoginListener;
import org.xchg.online.baseframe.listeners.ProgressListener;

/**
 * Created by rsankarx on 14/10/16.
 */
public interface LoginParentActivity {
    public LoginListener loginListener();
    public ProgressListener progressListener();
}
