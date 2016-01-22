package com.pygopar.ohmycommand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        Log.e(TAG, "SUP");
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
