package org.xchg.online.baseframe.activity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xchg.online.baseframe.R;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private FrameLayout mContentHolder;
    private RelativeLayout mToolbarLayout;
    private View mStatusBGView;
    private int mStatusBarHeight;
    private boolean mIsTranslucentActionBar = false;
    private Bundle mSavedInstanceState;
    public SwipeRefreshLayout mSwipeRefresh;
    public View mErrorLayout;
    protected Activity mActivity;
    private ProgressDialog mProgress;


    protected abstract String getScreenName();

    private View mProgressDialog;
    private RelativeLayout mProgressView;
    View rootLayout;
    View cardFace;
    ObjectAnimator anim;
    RelativeLayout layout;

    @Override
    public void setContentView(int layoutResID) {
        layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        mContentHolder = (FrameLayout) layout.findViewById(R.id.base_activity_content_holder);
        mToolbar = (Toolbar) layout.findViewById(R.id.base_toolbar);
        mToolbarLayout = (RelativeLayout) layout.findViewById(R.id.toolbar_layout);
        mToolbarTitle = (TextView) layout.findViewById(R.id.base_toolbar_title);
        mToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
        mStatusBGView = layout.findViewById(R.id.base_activity_status_bar_bg);
        mSwipeRefresh = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefresh);
        mErrorLayout = layout.findViewById(R.id.errorLayout);
        getLayoutInflater().inflate(layoutResID, mContentHolder, true);
        mStatusBarHeight = getInternalDimensionSize(getResources(), STATUS_BAR_HEIGHT_RES_NAME);
        mToolbar.setTitle("");
        mProgress = new ProgressDialog(this);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        super.setContentView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mMoEngageHelper.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    protected void setActionBar() {
        mToolbar.setVisibility(View.VISIBLE);
        mToolbarLayout.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    protected void setActionBarWithTranslucentStatus() {
        mIsTranslucentActionBar = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mToolbarLayout.getLayoutParams());
            layoutParams.setMargins(0, mStatusBarHeight, 0, 0);
            mToolbarLayout.setLayoutParams(layoutParams);
            mStatusBGView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mStatusBarHeight));
        }

        mToolbarLayout.setVisibility(View.VISIBLE);
        mToolbarTitle.setTextSize(16);
        mToolbarTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void setActionBarBackground(int color) {
        if (mIsTranslucentActionBar) {
            mStatusBGView.setBackgroundColor(color);
            mStatusBGView.setVisibility(View.VISIBLE);
        }
        mToolbar.setBackgroundColor(color);
    }

    protected void setActionBarTitle(String title) {
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText(title);
    }

    protected void setActionBarTitleColor(int color) {
        mToolbarTitle.setTextColor(color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
    }

    protected void showErrorLayout(){
        mErrorLayout.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startParentActivity();
                finish();  //TODO : change to navigate to parent activity
                overridePendingTransition(R.anim.slide_left_to_mid, R.anim.slide_mid_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setToolbarTitle(String title) {
        TextView titleBar = (TextView) findViewById(R.id.base_toolbar_title);
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setText(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_to_mid, R.anim.slide_mid_to_right);
    }

    public void startParentActivity(){
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        }
    }

    protected void removeSearchMenu() {
        invalidateOptionsMenu();
    }

    /**
     * call inside onCreateOptionsMenu after calling super.onCreateOptionMenu() and return true from the function*
     */
    protected void changeSearchIcon(int iconId) {
        invalidateOptionsMenu();
    }
    protected void hideActionBar(){
        mToolbarLayout.animate().translationY(-mToolbarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(1));
    }

    protected void showActionBar(){
        mToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
        mToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1)).start();
    }


    protected boolean isProgressDialogShowing(){
        return mProgressView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_mid, R.anim.slide_mid_to_left);
    }

    public void disableAllViews(View v){
        v.setEnabled(false);
        if(v instanceof ViewGroup){

            for (int i = 0; i < ((ViewGroup)v).getChildCount(); i++) {
                View view = ((ViewGroup)v).getChildAt(i);
                disableAllViews(v);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void showProgress(String msg) {
        mProgress.setMessage(msg);
        mProgress.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryLight)));
        mProgress.setIndeterminate(false);
        mProgress.show();
    }

    protected void dismissProgress() {
        mProgress.dismiss();
    }
}
