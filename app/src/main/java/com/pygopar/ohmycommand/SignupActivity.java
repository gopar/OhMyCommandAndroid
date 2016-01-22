package com.pygopar.ohmycommand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pygopar.api.OMCAPI;
import com.pygopar.constants.OMCConst;
import com.pygopar.helpers.Token;
import com.pygopar.helpers.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SignupActivity extends BaseActivity {
    private static final String TAG = SignupActivity.class.toString();

    @Bind(R.id.register_email)
    EditText emailET;
    @Bind(R.id.register_username)
    EditText usernameET;
    @Bind(R.id.register_password)
    EditText firstPassET;
    @Bind(R.id.register_second_password)
    EditText secondPassET;
    @Bind(R.id.btn_register)
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_login)
    public void onClickLinkLogin() {
        finish();
    }

    @OnClick(R.id.btn_register)
    public void onClickbtnRegister() {
        if (!validate())
            return;

        // Disable btn
        registerBtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String email = emailET.getText().toString();
        final String username = usernameET.getText().toString();
        final String password = firstPassET.getText().toString();

        // Start api call
        Retrofit apiCall = new Retrofit.Builder()
                .baseUrl(OMCConst.API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OMCAPI omcAPI = apiCall.create(OMCAPI.class);
        Call<Token> postNewUser = omcAPI.postNewUser(username, email, password);

        postNewUser.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Response<Token> response, Retrofit retrofit) {
                Token token = response.body();
                progressDialog.dismiss();

                if (response.isSuccess() && token != null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().putString(getResources().getString(R.string.pref_user_auth_token),
                            token.getToken()).apply();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    onFailure(new Exception());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.scrollview_signup),
                        getResources().getString(isNetworkAvailable()? R.string.something_wrong: R.string.are_you_connected),
                        Snackbar.LENGTH_LONG);
                snackbar.setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });

                progressDialog.dismiss();
                registerBtn.setEnabled(true);
                snackbar.show();
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        if (!isValidEmail(emailET.getText().toString())) {
            emailET.setError(getResources().getString(R.string.invalid_email));
            isValid = false;
        } else {
            emailET.setError(null);
        }

        if (usernameET.getText().toString().isEmpty()) {
            usernameET.setError(getResources().getString(R.string.cannot_be_empty));
            isValid = false;
        } else if (!usernameET.getText().toString().matches("^[a-zA-Z0-9]+$")) {
            usernameET.setError(getResources().getString(R.string.only_letters_and_numbers));
            isValid = false;
        } else {
            usernameET.setError(null);
        }

        String firstPass = firstPassET.getText().toString();
        String secondPass = secondPassET.getText().toString();
        if (firstPass.length() < 7){
            firstPassET.setError(getResources().getString(R.string.password_min));
            isValid = false;
        } else {
            isValid = true;
        }

        // 1st and 2nd password must match
        if (!firstPass.equals(secondPass)) {
            isValid = false;
            secondPassET.setError(getResources().getString(R.string.passwords_must_match));
        } else {
            secondPassET.setError(null);
        }

        return isValid;
    }
}
