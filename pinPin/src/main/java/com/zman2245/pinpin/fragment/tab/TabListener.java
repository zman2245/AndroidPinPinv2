package com.zman2245.pinpin.fragment.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.log.EventLog;

public class TabListener<T extends Fragment> implements ActionBar.TabListener
{
    private Fragment                        mFragment;
    private final SherlockFragmentActivity  mActivity;
    private final String                    mTag;
    private final Class<T>                  mClass;
    private final int                       mFlurryEventResId;

    public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz, int flurryEventResId)
    {
        mActivity           = activity;
        mTag                = tag;
        mClass              = clz;
        mFlurryEventResId   = flurryEventResId;

        mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft)
    {
        EventLog.trackEvent(mFlurryEventResId);

        // Check if the fragment is already initialized
        if (mFragment == null)
        {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            // mFragment.setProviderId(mTag); // id for event provider
            ft.add(R.id.tab_container, mFragment, mTag);
        }
        else
        {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft)
    {
        if (mFragment != null)
        {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft)
    {
        // not sure if this is correct
        ft.attach(mFragment);
    }
}