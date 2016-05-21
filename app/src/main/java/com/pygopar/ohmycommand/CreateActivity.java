package com.pygopar.ohmycommand;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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
                Log.w(TAG, "" + command);
                progressDialog.dismiss();

                if (response.isSuccess() && command != null){
                    // TODO: 5/20/16 Save to DB and update listview
                    
                }
            }

            @Override
            public void onFailure(Throwable t) {

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
