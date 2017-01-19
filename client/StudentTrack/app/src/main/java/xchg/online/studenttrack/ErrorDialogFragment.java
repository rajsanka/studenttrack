package xchg.online.studenttrack;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;

import xchg.online.studenttrack.utils.OnErrorDialogDismissedListener;
import xchg.online.studenttrack.utils.TrafficConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorDialogFragment extends DialogFragment {

    private OnErrorDialogDismissedListener mDismissedListener;

    public ErrorDialogFragment() { }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the error code and retrieve the appropriate dialog
        int errorCode = this.getArguments().getInt(TrafficConstants.DIALOG_ERROR);
        return GoogleApiAvailability.getInstance().getErrorDialog(
                this.getActivity(), errorCode, TrafficConstants.REQUEST_RESOLVE_ERROR);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDismissedListener != null) {
            mDismissedListener.onDialogDismissed();
        }
    }

    public void setDismissedListener(OnErrorDialogDismissedListener mDismissedListener) {
        this.mDismissedListener = mDismissedListener;
    }

}
