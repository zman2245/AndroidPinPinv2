package com.zman2245.pinpin.fragment.learn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.PracticeActivity;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.adapter.grid.AdapterGridReference;
import com.zman2245.pinpin.data.DataItemLearnFlow;
import com.zman2245.pinpin.util.content.UtilUi;

/**
 * A fragment for a single learn flow item (aka page)
 *
 * @author zack
 */
public class FragmentLearnFlowItem extends Fragment
{
    private static final String KEY_DATA = "data";

    /**
     * FragmentLearnFlowItem construction
     *
     * @param data  The data this fragment will present
     * @return A new instance of FragmentLearnFlowItem
     */
    public static FragmentLearnFlowItem newInstance(DataItemLearnFlow data)
    {
        FragmentLearnFlowItem frag  = new FragmentLearnFlowItem();
        Bundle args                 = new Bundle();

        args.putSerializable(KEY_DATA, data);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_learn_flow_item, container, false);

        TextView titleTextView  = (TextView)rootView.findViewById(R.id.title);
        TextView topTextView    = (TextView)rootView.findViewById(R.id.top_text);
        GridView gridView       = (GridView)rootView.findViewById(R.id.grid);
        TextView bottomTextView = (TextView)rootView.findViewById(R.id.bottom_text);
        DataItemLearnFlow data  = (DataItemLearnFlow)getArguments().getSerializable(KEY_DATA);

        // display a title if there is one
        if (TextUtils.isEmpty(data.title))
        {
            titleTextView.setVisibility(View.GONE);
        }
        else
        {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(data.title);
        }

        topTextView.setText(data.topText);
        bottomTextView.setText(data.bottomText);

        if (data.syllables == null || data.syllables.length <= 0)
        {
            gridView.setVisibility(View.GONE);
        }
        else
        {
            final AdapterGridReference adapter = new AdapterGridReference(data.syllables, inflater);

            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(adapter);
            gridView.setNumColumns(data.syllables[0].length);
            UtilUi.fixGridViewWidth(gridView, data.syllables[0].length, getResources().getDimensionPixelSize(R.dimen.gridview_words_cell_width));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String word = (String)adapter.getItem(position);

                    String parentWord = AppPinPin.sSoundMapReverse.get(word);

                    Intent intent = new Intent(getActivity(), PracticeActivity.class);
                    intent.putExtra("word", parentWord);
                    startActivity(intent);

                    getActivity().overridePendingTransition(R.anim.fade_in_default, 0);
                }
            });
        }

        return rootView;
    }
}
