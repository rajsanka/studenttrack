package org.xchg.online.baseframe.listeners;

/**
 * Created by rsankarx on 14/10/16.
 */
public interface LoginListener {
    public void onLoginSuccess();
    public void onRegisterSuccess();
    public void onVerifySuccess();
    public void onError();

    public void showRegister();
    public void showLogin();
    public void showVerify();
}
