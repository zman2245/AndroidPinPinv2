package com.zman2245.pinpin.fragment.quiz;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.adapter.grid.AdapterGridQuizItem;
import com.zman2245.pinpin.data.DataItemQuiz;
import com.zman2245.pinpin.fragment.event.Event;
import com.zman2245.pinpin.fragment.event.EventData;
import com.zman2245.pinpin.fragment.event.EventType;
import com.zman2245.pinpin.fragment.event.FragmentEventListener;
import com.zman2245.pinpin.model.ModelQuizQuestion;
import com.zman2245.pinpin.util.audio.UtilAudioPlayer;
import com.zman2245.pinpin.view.listitem.ViewQuizChoice;

/**
 * A fragment for a single quiz question
 *
 * @author zack
 */
public class FragmentQuizQuestion extends Fragment implements OnItemClickListener
{
    private static final String KEY_DATA = "data";

    private GridView            mGrid;
    private TextView            mAnswerText;
    private Button              mContinueButton;

    private AdapterGridQuizItem mAdapter;
    private DataItemQuiz        mData;
    private ModelQuizQuestion   mModel;
    private int                 mNumSubQuestions;
    private boolean[]           mCorrectStatuses;

    /**
     * FragmentQuizItem construction
     *
     * @param data
     *            The data this fragment will present
     * @return A new instance of FragmentLearnFlowItem
     */
    public static FragmentQuizQuestion newInstance(DataItemQuiz data)
    {
        FragmentQuizQuestion frag = new FragmentQuizQuestion();
        Bundle args = new Bundle();

        args.putSerializable(KEY_DATA, data);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = (DataItemQuiz) getArguments().get(KEY_DATA);
        mModel = new ModelQuizQuestion(mData);
        mNumSubQuestions = mData.answers.length;

        mCorrectStatuses = new boolean[mData.answers.length];
        for (int i = 0; i < mData.answers.length; i++)
        {
            mCorrectStatuses[i] = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_quiz_item, container, false);

        mContinueButton = (Button) rootView.findViewById(R.id.btn_continue);
        mAnswerText = (TextView) rootView.findViewById(R.id.answer_text);
        mGrid = (GridView) rootView.findViewById(R.id.grid_choice);
        mAdapter = new AdapterGridQuizItem(mData.choices, inflater);

        mGrid.setNumColumns(mNumSubQuestions);
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(this);

        mContinueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put(EventData.DATA_KEY_MODEL, mModel);
                Event event = new Event(EventType.QUIZ_CONTINUE, data);
                sendEvent(event);
            }
        });

        View answerView = rootView.findViewById(R.id.answer);
        answerView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playAnswer();
            }
        });

        // if already answered populate the answer and disable choices
        if (mData.alreadyComplete)
        {
            mAnswerText.setText(mModel.getAnswerText());
            for (int i = 0; i < mNumSubQuestions; i++)
            {
                mAdapter.setColumnEnabled(i, false);
            }
            showContinueButton();
        }

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        playAnswer();
    }

    // OnItemClickListener impl

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ViewQuizChoice choiceView = (ViewQuizChoice) view;
        String word = (String) mAdapter.getItem(position);
        int subQuestion = getSubQuestionNum(position);

        boolean isCorrect = mModel.answer(word, subQuestion);
        mCorrectStatuses[subQuestion] = isCorrect;

        if (isCorrect)
        {
            mAnswerText.setText(mModel.getAnswerText());
            choiceView.setCorrect();
            mAdapter.setColumnEnabled(subQuestion, false);

            if (allCorrect())
                showContinueButton();
        }
        else
        {
            choiceView.setIncorrect();
        }
    }

    // private helpers

    private boolean allCorrect()
    {
        for (int i = 0; i < mCorrectStatuses.length; i++)
        {
            if (!mCorrectStatuses[i])
                return false;
        }

        return true;
    }

    private int getSubQuestionNum(int position)
    {
        return position % mNumSubQuestions;
    }

    private void playAnswer()
    {
        int[] resIds = new int[mData.answers.length];
        int i = 0;
        for (String answer : mData.answers)
        {
            resIds[i] = AppPinPin.getAudioMapper().getResourceForString(answer);
            i++;
        }

        UtilAudioPlayer.playSounds(resIds);
    }

    private void showContinueButton()
    {
        mContinueButton.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_default);
        mContinueButton.startAnimation(anim);
    }

    /**
     * Send an event to this fragment's parent (could be a parent fragment or an
     * activity)
     *
     * @param event
     *            The event to send
     */
    private void sendEvent(Event event)
    {
        Fragment frag = getParentFragment();
        if (frag != null && frag instanceof FragmentEventListener)
        {
            ((FragmentEventListener) frag).handleEvent(event);
            return;
        }

        Activity activity = getActivity();
        if (activity != null && activity instanceof FragmentEventListener)
        {
            ((FragmentEventListener) activity).handleEvent(event);
        }
    }
}
