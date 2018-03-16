package com.wachat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.util.Prefs;

public class ActivityTutorial extends BaseActivity {

    private Prefs mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_tutorial);

        ImageView iv_tutorial = (ImageView) findViewById(R.id.iv_tutorial);
        mPrefs = Prefs.getInstance(this);
        iv_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.setFirstTimeTutorialShown();
                finish();
            }
        });


    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    protected void CommonMenuClcik() {

    }

}
