package com.pygopar.ohmycommand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.list_commands)
    ListView commandsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setDummyData();
    }

    private void setDummyData() {
        String[] items = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, items);
        commandsList.setAdapter(adapter);
    }

    @OnItemClick(R.id.list_commands)
    public void onClickCommand(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }
}
