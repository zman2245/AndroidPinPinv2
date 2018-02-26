package com.zman2245.pinpin.fragment.learn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemLearnFlow;

/**
 * A fragment for a single learn flow item for the "Introduction" section
 * 
 * @author zack
 */
public class FragmentIntroductionLearnFlowItem extends Fragment
{
    private static final String KEY_DATA = "data";

    /**
     * FragmentIntroductionLearnFlowItem construction
     * 
     * @param data
     *            The data this fragment will present
     * @return A new instance of FragmentIntroductionLearnFlowItem
     */
    public static FragmentIntroductionLearnFlowItem newInstance(DataItemLearnFlow data)
    {
        FragmentIntroductionLearnFlowItem frag = new FragmentIntroductionLearnFlowItem();
        Bundle args = new Bundle();

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
        View rootView = inflater.inflate(R.layout.fragment_introduction_learn_flow_item, container, false);

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
        TextView topTextView = (TextView) rootView.findViewById(R.id.top_text);
        TextView bottomTextView = (TextView) rootView.findViewById(R.id.bottom_text);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        DataItemLearnFlow data = (DataItemLearnFlow) getArguments().getSerializable(KEY_DATA);

        titleTextView.setText(data.title);
        topTextView.setText(data.topText);
        bottomTextView.setText(data.bottomText);
        imageView.setImageResource(data.image);

        return rootView;
    }
}
