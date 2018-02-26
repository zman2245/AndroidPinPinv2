package com.zman2245.pinpin.fragment.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.adapter.list.AdapterListLearn;
import com.zman2245.pinpin.data.DataItemLearnFlow;
import com.zman2245.pinpin.fragment.PinBaseFragment;
import com.zman2245.pinpin.fragment.event.Event;
import com.zman2245.pinpin.fragment.event.FragmentEventListener;
import com.zman2245.pinpin.fragment.learn.FragmentLearnFlow;
import com.zman2245.pinpin.log.EventLog;
import com.zman2245.pinpin.util.content.UtilContentStrings;

public class FragmentTabLearn extends PinBaseFragment implements FragmentEventListener
{
    private ListView            mList;
    private AdapterListLearn    mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main_learn, container, false);

        setHasOptionsMenu(true);

        mList       = (ListView) rootView.findViewById(R.id.list);
        mAdapter    = new AdapterListLearn(getActivity(), inflater);

        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                navigateToLearnSection(position);
            }
        });

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getChildFragmentManager().findFragmentByTag("learn_flow") == null)
            enableHomeAsUp(false);
        else
            enableHomeAsUp(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            endLearn();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // PinBaseFragment overrides

    @Override
    public boolean onBackPressed()
    {
        Fragment frag = getChildFragmentManager().findFragmentByTag("learn_flow");
        if (frag != null && frag instanceof PinBaseFragment)
        {
            return ((PinBaseFragment) frag).onBackPressed();
        }

        return false;
    }

    // FragmentEventListener impl

    @Override
    public void handleEvent(Event event)
    {
        switch (event.type)
        {
        case LEARN_END:
            endLearn();
            break;

        default:
        }
    }

    private void navigateToLearnSection(int index)
    {
        DataItemLearnFlow[] datas = UtilContentStrings.getLearnSectionData(index);

        FragmentLearnFlow frag = FragmentLearnFlow.newInstance(datas, index == 0);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.container, frag, "learn_flow");
        ft.commit();

        enableHomeAsUp(true);

        if (index == 0)
            EventLog.trackEvent(R.string.flurry_event_section_intro);
    }

    private void endLearn()
    {
        FragmentManager fm = getChildFragmentManager();
        Fragment frag = fm.findFragmentByTag("learn_flow");

        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(frag);
        ft.commitAllowingStateLoss();

        enableHomeAsUp(false);

        mAdapter.notifyDataSetChanged();
    }
}
