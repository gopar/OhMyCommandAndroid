package com.pygopar.ohmycommand;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class BaseActivity extends AppCompatActivity {
    /**
     * Checks to see if user is connected to internet. Returns true if it is.
     * @return Boolean
     */
    protected boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Returns true if the email is valid. Otherwise, return false
     * @param email to be checked
     * @return boolean
     */
    protected boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
