package com.pygopar.ohmycommand;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pygopar.api.OMCAPI;
import com.pygopar.constants.OMCConst;
import com.pygopar.helpers.Command;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.toString();
    private List<Command> commandList = new ArrayList<>();

    @Bind(R.id.list_commands)
    ListView commandsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fetchServerData();
    }

    private void fetchServerData() {
        // Check if DB is not empty
        if (!Command.listAll(Command.class).isEmpty()) {
            populateWithDBEntries();
            return;
        }
        Log.e(TAG, "Fetching");
        // Get token from prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString(getResources().getString(R.string.pref_user_auth_token), null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Commands...");
        progressDialog.show();

        Retrofit apiCall = new Retrofit.Builder()
                .baseUrl(OMCConst.API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OMCAPI omcAPI = apiCall.create(OMCAPI.class);
        Call<List<Command>> getCommands = omcAPI.getCommands(OMCConst.API_TOKEN_HEADER + token);

        getCommands.enqueue(new Callback<List<Command>>() {
            @Override
            public void onResponse(Response<List<Command>> response, Retrofit retrofit) {
                progressDialog.dismiss();
                List<Command> commands = response.body();

                ArrayList<String> commandText = new ArrayList<String>();

                if (response.isSuccess() && commands != null) {
                    for (Command c: commands) {
                        c.save();
                        commandList.add(c);
                        commandText.add(c.command);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.command_textview, R.id.command_textview, commandText);

                    commandsListView.setAdapter(adapter);
                } else {
                    onFailure(new Exception());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "ERROR");
                Log.e(TAG, t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Could not retrieve commands",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateWithDBEntries() {
        List<Command> commands = Command.listAll(Command.class);
        ArrayList<String> items = new ArrayList<>();

        for (Command c : commands) {
            commandList.add(c);
            items.add(c.command);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.command_textview, R.id.command_textview, items);
        commandsListView.setAdapter(adapter);
    }

    @OnItemClick(R.id.list_commands)
    public void onClickCommand(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, commandList.get(position).command, Toast.LENGTH_SHORT).show();
    }
}
