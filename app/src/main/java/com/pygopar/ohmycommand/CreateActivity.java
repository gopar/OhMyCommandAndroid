package com.pygopar.ohmycommand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pygopar.api.OMCAPI;
import com.pygopar.constants.OMCConst;
import com.pygopar.helpers.Command;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CreateActivity extends BaseActivity {
    private static final String TAG = CreateActivity.class.toString();
    @Bind(R.id.create_add)
    Button createBtn;
    @Bind(R.id.create_command)
    EditText commandET;
    @Bind(R.id.create_os)
    EditText osET;
    @Bind(R.id.create_version)
    EditText versionET;
    @Bind(R.id.create_note)
    EditText noteET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.create_cancel)
    public void onCancel() {
        finish();
    }

    @OnClick(R.id.create_add)
    public void OnAdd() {
        Log.e(TAG, "on Add");
        if (!validate())
            return;

        // Disable button
        createBtn.setEnabled(false);

        // Get input fields
        final String command = commandET.getText().toString();
        final String os = osET.getText().toString();
        final String version = versionET.getText().toString();
        final String note = noteET.getText().toString();

        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating...");
        progressDialog.show();

        // Grab auth token from Shared Prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String token = prefs.getString(getResources().getString(R.string.pref_user_auth_token), null);

        // Start to api call
        Retrofit apiCall = new Retrofit.Builder()
                .baseUrl(OMCConst.API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OMCAPI omcAPI = apiCall.create(OMCAPI.class);
        Call<Command> getCommand = omcAPI.postCommand(
                OMCConst.API_TOKEN_HEADER + token,
                new Command(command, os, version, note));

        // Save activity for use in API call
        final BaseActivity activity = this;

        getCommand.enqueue(new Callback<Command>() {
            @Override
            public void onResponse(Response<Command> response, Retrofit retrofit) {
                Log.w(TAG, "OnResponse");
                Command command = response.body();
                Log.w(TAG, "Command id = " + command.myId);
                progressDialog.dismiss();

                if (response.isSuccess() && command != null){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("command", command.command);
                    returnIntent.putExtra("os", command.os);
                    returnIntent.putExtra("version", command.version);
                    returnIntent.putExtra("note", command.note);
                    setResult(MainActivity.RESULT_OK, returnIntent);
                    Toast.makeText(activity, "Command Created!", Toast.LENGTH_LONG).show();
                    finish();
                } else
                    onFailure(new Exception());
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                setResult(MainActivity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        final String command = commandET.getText().toString();
        final String os = osET.getText().toString();

        if (command.isEmpty()) {
            commandET.setError(getResources().getString(R.string.cannot_be_empty));
            isValid = false;
        } else
            commandET.setError(null);

        return isValid;
    }
}
