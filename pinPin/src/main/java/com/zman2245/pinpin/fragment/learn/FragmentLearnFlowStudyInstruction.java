package com.zman2245.pinpin.fragment.learn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemPractice;
import com.zman2245.pinpin.fragment.PinBaseFragment;
import com.zman2245.pinpin.model.ModelPracticeSet;

/**
 * A fragment for the "Study this section" page
 * 
 * @author zack
 */
public class FragmentLearnFlowStudyInstruction extends PinBaseFragment
{
    private static final String KEY_DATA = "data";

    private ModelPracticeSet    mModel;

    /**
     * FragmentLearnFlowStudyInstruction construction
     * 
     * @return A new instance of FragmentLearnFlowStudyInstruction
     */
    public static FragmentLearnFlowStudyInstruction newInstance(DataItemPractice[] datas)
    {
        FragmentLearnFlowStudyInstruction frag = new FragmentLearnFlowStudyInstruction();

        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, datas);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DataItemPractice[] datas = (DataItemPractice[]) getArguments().get(KEY_DATA);
        mModel = new ModelPracticeSet(datas);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_learn_flow_study_instruction, container, false);

        Button study = (Button) rootView.findViewById(R.id.btn_study);
        study.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                moveToNextPractice();
            }
        });

        return rootView;
    }

    private void moveToNextPractice()
    {
        Fragment frag;
        DataItemPractice data = mModel.next();

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (data == null)
        {
            // End of practice
            ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
            frag = fm.findFragmentByTag("flow");
            ft.remove(frag);
            return;
        }
        else
        {
            frag = FragmentPractice.newInstance(data);
        }

        if (fm.findFragmentByTag("flow") == null)
        {
            ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
            ft.add(R.id.container, frag, "flow");
        }
        else
        {
            ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            ft.replace(R.id.container, frag, "flow");
        }

        ft.commit();
    }
}
