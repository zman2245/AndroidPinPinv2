package com.zman2245.pinpin.fragment.learn;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.Registry;
import com.zman2245.pinpin.adapter.viewpager.PagerAdapterIntroductionLearnFlow;
import com.zman2245.pinpin.adapter.viewpager.PagerAdapterLearnFlow;
import com.zman2245.pinpin.data.DataItemLearnFlow;
import com.zman2245.pinpin.fragment.PinBaseFragment;
import com.zman2245.pinpin.fragment.event.Event;
import com.zman2245.pinpin.fragment.event.EventType;
import com.zman2245.pinpin.log.EventLog;
import com.zman2245.pinpin.view.pagecontrol.PageControl;

/**
 * A fragment for a learn flow
 *
 * Contains some flow of views
 *
 * @author zack
 */
public class FragmentLearnFlow extends PinBaseFragment
{
    public static final String KEY_DATAS = "datas";
    public static final String KEY_INTRO = "isIntro";

    private ViewPager           mPager;
    private DataItemLearnFlow[] mDatas;
    private String              mId;

    /**
     * FragmentLearnFlow construction
     *
     * @param title
     *            The main title of the learn flow
     * @param topBodies
     *            The top text sections of the learn flow
     * @param bottomBodies
     *            The bottom text sections of the learn flow
     * @return A new instance of FragmentLearnFlow
     */
    public static FragmentLearnFlow newInstance(DataItemLearnFlow[] datas, boolean isIntro)
    {
        FragmentLearnFlow frag = new FragmentLearnFlow();
        Bundle args = new Bundle();

        args.putSerializable(KEY_DATAS, datas);
        args.putBoolean(KEY_INTRO, isIntro);

        frag.setArguments(args);

        return frag;
    }

    @Override
    public boolean onBackPressed()
    {
        int currentPos = mPager.getCurrentItem();
        if (currentPos == 0)
        {
            Event event = new Event(EventType.LEARN_END);
            sendEvent(event);
        }
        else
        {
            mPager.setCurrentItem(currentPos - 1);
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_learn_flow, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        final PageControl pageControl = (PageControl) rootView.findViewById(R.id.page_control);

        mDatas = getData(getArguments().getSerializable(KEY_DATAS));
        mId = mDatas[0].section_id;

        // the intro flow has a much different format, so it is separated
        FragmentStatePagerAdapter adapter;
        if (getArguments().getBoolean(KEY_INTRO))
            adapter = new PagerAdapterIntroductionLearnFlow(getChildFragmentManager(), mDatas);
        else
            adapter = new PagerAdapterLearnFlow(getChildFragmentManager(), mDatas);

        mPager.setAdapter(adapter);
        pageControl.setPageNumber(adapter.getCount());
        pageControl.setSelectedPageIndex(0);
        Registry.sProgressFactory.markLearnProgress(mId, 0, mDatas.length);

        mPager.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int index)
            {
                pageControl.setSelectedPageIndex(index);

                Registry.sProgressFactory.markLearnProgress(mId, index, mDatas.length);

                if ((index >= (mDatas.length - 1)))
                    EventLog.trackEvent(R.string.flurry_event_section_complete);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });

        return rootView;
    }

    /**
     * Can't reliably cast an array of serialized objects to the array type
     *
     * @param obj
     * @return
     */
    private DataItemLearnFlow[] getData(Object obj)
    {
        Object[] objects = (Object[])obj;
        DataItemLearnFlow[] datas = new DataItemLearnFlow[objects.length];

        for (int i = 0; i < datas.length; i++)
        {
            datas[i] = (DataItemLearnFlow)objects[i];
        }

        return datas;
    }
}
