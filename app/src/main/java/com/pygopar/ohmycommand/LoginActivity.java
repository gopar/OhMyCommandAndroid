package com.pygopar.ohmycommand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pygopar.api.OMCAPI;
import com.pygopar.constants.OMCConst;
import com.pygopar.helpers.Token;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.toString();

    @Bind(R.id.login_input_username)
    EditText usernameET;
    @Bind(R.id.login_input_password)
    EditText passwordET;
    @Bind(R.id.btn_login)
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Check if user is already logged in
        final String authToken = getResources().getString(R.string.pref_user_auth_token);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String token = prefs.getString(authToken, null);
        if (token != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }
    }

    @OnClick(R.id.link_signup)
    public void onClickLinkSignup() {
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void onCLickBtnLogin () {
        Log.e(TAG, "HELLO");
        if (!validate())
            return;

        // disable btn to not let user send more than one request
        loginBtn.setEnabled(false);

        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String username = usernameET.getText().toString();
        final String pass = passwordET.getText().toString();

        // Start to call tu authh against server
        Retrofit apiCall = new Retrofit.Builder()
                .baseUrl(OMCConst.API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OMCAPI omcAPI = apiCall.create(OMCAPI.class);
        Call<Token> getToken = omcAPI.getToken(username, pass);

        // Save this activity for use in api call
        final BaseActivity activity = this;


        getToken.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Response<Token> response, Retrofit retrofit) {
                Token token = response.body();
                progressDialog.dismiss();

                if (response.isSuccess() && token != null) {
                    // Save token to shared prefs
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().putString(getResources().getString(R.string.pref_user_auth_token),
                            token.getToken()).apply();

                    // Start new activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    onFailure(new Exception());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                final boolean isThereNetwork = activity.isNetworkAvailable();
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.scrollview_login),
                        getResources().getString(isThereNetwork? R.string.error_login: R.string.are_you_connected),
                        Snackbar.LENGTH_LONG);
                snackbar.setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();

                // Enable bt again to let user try another time
                loginBtn.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        final String username = usernameET.getText().toString();
        final String pass = passwordET.getText().toString();

        if (username.isEmpty()) {
            usernameET.setError(getResources().getString(R.string.cannot_be_empty));
            isValid = false;
        } else {
            usernameET.setError(null);
        }

        if (pass.isEmpty()) {
            passwordET.setError(getResources().getString(R.string.cannot_be_empty));
            isValid = false;
        } else {
            passwordET.setError(null);
        }

        return isValid;
    }
}
