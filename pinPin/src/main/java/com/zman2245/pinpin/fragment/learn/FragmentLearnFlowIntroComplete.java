package com.zman2245.pinpin.fragment.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.fragment.PinBaseFragment;

/**
 * A fragment for the "That's it!" page at the end of the introduction
 *
 * @author zack
 */
public class FragmentLearnFlowIntroComplete extends PinBaseFragment
{
    /**
     * FragmentLearnFlowIntroComplete construction
     *
     * @return A new instance of FragmentLearnFlowIntroComplete
     */
    public static FragmentLearnFlowIntroComplete newInstance()
    {
        return new FragmentLearnFlowIntroComplete();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_learn_flow_intro_complete, container, false);

        return rootView;
    }
}
