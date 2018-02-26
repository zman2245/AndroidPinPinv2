package com.zman2245.pinpin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.zman2245.pinpin.data.DataItemPractice;
import com.zman2245.pinpin.fragment.learn.FragmentPractice;
import com.zman2245.pinpin.log.EventLog;

public class PracticeActivity extends SherlockFragmentActivity
{
    private FragmentPractice mFrag;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // the window background/decor view backgrounds are not visible
        getWindow().setBackgroundDrawable(null);
        getWindow().getDecorView().setBackgroundDrawable(null);

        setContentView(R.layout.activity_practice);

        Intent intent       = getIntent();
        DataItemPractice data = new DataItemPractice();
        data.word = intent.getStringExtra("word");;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mFrag = FragmentPractice.newInstance(data);
        ft.add(R.id.container, mFrag, "frag_practice");
        ft.commit();

        EventLog.trackEvent(R.string.flurry_event_sound_practice);
    }

    @Override
    public void onBackPressed()
    {
        mFrag.animateOut();
    }
}