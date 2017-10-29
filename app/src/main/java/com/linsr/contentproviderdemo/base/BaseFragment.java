package com.linsr.contentproviderdemo.base;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

/**
 * Description
 @author Linsr
 */

public class BaseFragment extends Fragment {

    protected void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

}
