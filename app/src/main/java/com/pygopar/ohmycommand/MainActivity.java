package com.pygopar.ohmycommand;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pygopar.api.OMCAPI;
import com.pygopar.constants.OMCConst;
import com.pygopar.helpers.Command;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.toString();
    private List<Command> commandList = new ArrayList<>();

    public final static int RESULT_OK = 1;
    public final static int RESULT_CANCELED = 2;

    @Bind(R.id.list_commands)
    ListView commandsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fetchServerData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu:
                // Delete token pref
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(getResources().getString(R.string.pref_user_auth_token));
                editor.apply();
                // Delete entries
                Command.deleteAll(Command.class);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final String command = data.getStringExtra("command");
            final String os = data.getStringExtra("os");
            final String version = data.getStringExtra("version");
            final String note = data.getStringExtra("note");

            Command commandObj = new Command(command, os , version, note);
            commandObj.save();
            commandList.add(commandObj);

            ArrayList<String> items = new ArrayList<>();

            for (Command c : commandList)
                items.add(c.command);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.command_textview, R.id.command_textview, items);

            commandsListView.setAdapter(adapter);
        }

    }

    @OnItemClick(R.id.list_commands)
    public void onClickCommand(AdapterView<?> parent, View view, int position, long id) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", commandList.get(position).command);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    @OnItemLongClick(R.id.list_commands)
    public boolean onLongClickCommand(AdapterView<?> adapterView, View view, final int position, long id) {
        final Command commandObj = commandList.get(position);
        final String command =  commandObj.command;
        final BaseActivity activity = this;
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Log.w(TAG, "Si");
                        // Then update listview accordingly
                        // Get token from prefs
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                        String token = prefs.getString(getResources().getString(R.string.pref_user_auth_token), null);
                        Log.w(TAG, "T " +token);

                        final ProgressDialog progressDialog = new ProgressDialog(activity);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Deleting Command...");
                        progressDialog.show();

                        Retrofit apiCall = new Retrofit.Builder()
                                .baseUrl(OMCConst.API_URL_BASE)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        OMCAPI omcAPI = apiCall.create(OMCAPI.class);
                        Call<ResponseBody> deleteCommand = omcAPI.deleteCommand(OMCConst.API_TOKEN_HEADER + token, commandObj.getId());

                        deleteCommand.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    // Delete command from DB, update ListView
                                    commandObj.delete();
                                    commandList.remove(position);

                                    ArrayList<String> items = new ArrayList<>();

                                    for (Command c : commandList)
                                        items.add(c.command);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                                            R.layout.command_textview, R.id.command_textview, items);
                                    commandsListView.setAdapter(adapter);
                                    progressDialog.dismiss();
                                }
                                else
                                    onFailure(new Exception());
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(activity, "Unable to delete", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing.
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete?\n" + (command.length() > 11? command.substring(0, 10): command))
                .setPositiveButton("Yes", dialogListener).setNegativeButton("No", dialogListener).show();
        return true;
    }

    @OnClick(R.id.fab_create)
    public void onFabClick() {
        startActivityForResult(new Intent(getApplicationContext(), CreateActivity.class), 1);
    }
}
