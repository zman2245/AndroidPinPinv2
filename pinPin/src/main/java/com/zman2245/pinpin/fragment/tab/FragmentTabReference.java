package com.zman2245.pinpin.fragment.tab;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.PracticeActivity;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.adapter.grid.AdapterGridReference;
import com.zman2245.pinpin.fragment.PinBaseFragment;
import com.zman2245.pinpin.log.EventLog;
import com.zman2245.pinpin.util.audio.Tone;
import com.zman2245.pinpin.util.audio.UtilAudioPlayer;
import com.zman2245.pinpin.util.content.UtilContentStrings;
import com.zman2245.pinpin.util.content.UtilUi;

public class FragmentTabReference extends PinBaseFragment
{
    private GridView             mGridView;
    private LinearLayout         mTopbar;
    private AdapterGridReference mAdapter;

    private MenuItem mMenuItemTone;

    private Tone mCurrentTone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main_reference, container, false);

        setHasOptionsMenu(true);

        mTopbar   = (LinearLayout) rootView.findViewById(R.id.topbar);
        mGridView = (GridView) rootView.findViewById(R.id.grid);

        Tone tone = mCurrentTone == null ? Tone.FIRST : mCurrentTone;
        if (savedInstanceState != null && savedInstanceState.containsKey("tone"))
            tone = (Tone)savedInstanceState.getSerializable("tone");
        setContentTone(tone);

        mGridView.setOnItemClickListener(new GridView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                EventLog.trackEvent(R.string.flurry_event_reference_chart_listen);

                String word = (String) mAdapter.getItem(position);

                int resId = AppPinPin.getAudioMapper().getResourceForString(word);

                UtilAudioPlayer.playSound(resId);
            }
        });
        mGridView.setOnItemLongClickListener(new GridView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                EventLog.trackEvent(R.string.flurry_event_reference_chart_practice);

                navigateToPractice(position);

                return true;
            }
        });

        initTopbar();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putSerializable("tone", mCurrentTone);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        enableHomeAsUp(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_reference, menu);

        mMenuItemTone = menu.findItem(R.id.menu_item_tone);

        if (mCurrentTone != null)
            mMenuItemTone.setIcon(mCurrentTone.refWhiteIcon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.menu_item_tone_first:
            setContentTone(Tone.FIRST);
            return true;

        case R.id.menu_item_tone_second:
            setContentTone(Tone.SECOND);
            return true;

        case R.id.menu_item_tone_third:
            setContentTone(Tone.THIRD);
            return true;

        case R.id.menu_item_tone_fourth:
            setContentTone(Tone.FOURTH);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void setContentTone(Tone tone)
    {
        EventLog.trackEvent(R.string.flurry_event_reference_tone);

        mCurrentTone = tone;

        String[][] content  = UtilContentStrings.getReferenceStrings(tone);
        mAdapter            = new AdapterGridReference(content, getActivity().getLayoutInflater());

        UtilUi.fixGridViewWidth(mGridView, content[0].length, getResources().getDimensionPixelSize(R.dimen.gridview_words_cell_width));
        mGridView.setNumColumns(content[0].length);
        mGridView.setAdapter(mAdapter);

        // for devices that have a hard menu button, the menu is only created
        // once the menu button is pressed, so the menu item for the tone could
        // be null
        if (mMenuItemTone != null)
            mMenuItemTone.setIcon(tone.refWhiteIcon);
    }

    private void initTopbar()
    {
        String[] topbarWords = AppPinPin.getStringArray(R.array.reference_topbar);

        for (String word : topbarWords)
        {
            TextView txtView = new TextView(getActivity());
            txtView.setText(word);
            txtView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.gridview_words_cell_width),
                    getResources().getDimensionPixelSize(R.dimen.gridview_words_cell_height));
//            txtView.setTextSize(getResources().getDimension(R.dimen.textsize_reference_topbar));
            txtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            txtView.setLayoutParams(lps);
            mTopbar.addView(txtView);
        }
    }

    private void navigateToPractice(int index)
    {
        String word = (String)mAdapter.getItem(index);

        String parentWord = AppPinPin.sSoundMapReverse.get(word);

        Intent intent = new Intent(getActivity(), PracticeActivity.class);
        intent.putExtra("word", parentWord);
        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.fade_in_default, 0);
    }
}
