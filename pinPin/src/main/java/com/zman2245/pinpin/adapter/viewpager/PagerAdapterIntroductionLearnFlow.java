package com.zman2245.pinpin.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemLearnFlow;
import com.zman2245.pinpin.fragment.learn.FragmentIntroductionLearnFlowItem;
import com.zman2245.pinpin.fragment.learn.FragmentLearnFlowIntroComplete;

/**
 * A view pager adapter for the Introduction Learn flow
 * 
 * @author zack
 */
public class PagerAdapterIntroductionLearnFlow extends FragmentStatePagerAdapter
{
    DataItemLearnFlow[] mData;

    public PagerAdapterIntroductionLearnFlow(FragmentManager fm, DataItemLearnFlow[] data)
    {
        super(fm);

        mData = data;

        if (mData.length != 4)
            throw new IllegalStateException("intro data is the wrong size");

        // TODO: hack in the title and image resources for the introduction
        mData[0].title = AppPinPin.getAppResources().getString(R.string.learn_intro_title_1);
        mData[1].title = AppPinPin.getAppResources().getString(R.string.learn_intro_title_2);
        mData[2].title = AppPinPin.getAppResources().getString(R.string.learn_intro_title_3);
        mData[3].title = AppPinPin.getAppResources().getString(R.string.learn_intro_title_4);

        mData[0].image = R.drawable.learn_intro_1;
        mData[1].image = R.drawable.learn_intro_2;
        mData[2].image = R.drawable.learn_intro_3;
        mData[3].image = R.drawable.learn_intro_4;
    }

    @Override
    public Fragment getItem(int index)
    {
        if (index == mData.length)
            return FragmentLearnFlowIntroComplete.newInstance();

        return FragmentIntroductionLearnFlowItem.newInstance(mData[index]);
    }

    @Override
    public int getCount()
    {
        // +1 for the study section at the end
        return mData.length + 1;
    }
}
