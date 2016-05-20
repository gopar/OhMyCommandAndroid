package com.pygopar.ohmycommand;

import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateActivity extends BaseActivity {

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
}
