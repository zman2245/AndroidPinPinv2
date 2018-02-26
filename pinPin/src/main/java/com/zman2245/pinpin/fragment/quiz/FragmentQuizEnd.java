package com.zman2245.pinpin.fragment.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemEndQuiz;
import com.zman2245.pinpin.fragment.PinBaseFragment;
import com.zman2245.pinpin.fragment.event.Event;
import com.zman2245.pinpin.fragment.event.EventType;
import com.zman2245.pinpin.model.ModelQuizEnd;

/**
 * A fragment for the end of a quiz
 * 
 * @author zack
 */
public class FragmentQuizEnd extends PinBaseFragment
{
    private static final String KEY_DATA = "data";

    private ModelQuizEnd        mModel;

    /**
     * FragmentQuizItem construction
     * 
     * @param data
     *            The data this fragment will present
     * @return A new instance of FragmentLearnFlowItem
     */
    public static FragmentQuizEnd newInstance(DataItemEndQuiz data)
    {
        FragmentQuizEnd frag = new FragmentQuizEnd();
        Bundle args = new Bundle();

        args.putSerializable(KEY_DATA, data);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DataItemEndQuiz data = (DataItemEndQuiz) getArguments().get(KEY_DATA);

        mModel = new ModelQuizEnd(data.correctPct, data.incorrectPct);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_quiz_end, container, false);

        ((TextView) rootView.findViewById(R.id.title)).setText(mModel.getTitle());
        ((TextView) rootView.findViewById(R.id.subtitle)).setText(mModel.getSubtitle());
        ((ImageView) rootView.findViewById(R.id.img_trophy)).setImageResource(mModel.getTrophyResourceId());
        ((TextView) rootView.findViewById(R.id.correct_pct)).setText(mModel.getCorrectPctText());
        ((TextView) rootView.findViewById(R.id.incorrect_pct)).setText(mModel.getIncorrectPctText());

        Button doneButton = (Button) rootView.findViewById(R.id.btn_done);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Event event = new Event(EventType.QUIZ_END);
                sendEvent(event);
            }
        });

        return rootView;
    }
}
