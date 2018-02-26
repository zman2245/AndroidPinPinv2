package com.zman2245.pinpin.model;

import android.content.res.Resources;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;

/**
 * Quiz end model
 *
 * @author Zack
 */
public class ModelQuizEnd
{
    private final float mCorrectPct;
    private final float mIncorrectPct;

    public ModelQuizEnd(float correctPct, float incorrectPct)
    {
        mCorrectPct     = correctPct;
        mIncorrectPct   = incorrectPct;
    }

    /**
     * Tells whether or not the quiz was passed or failed
     *
     * @return
     */
    public boolean didPass()
    {
        return mCorrectPct >= .6f;
    }

    public String getTitle()
    {
        Resources res = AppPinPin.getAppResources();

        if (mCorrectPct == 1.0f)
            return res.getString(R.string.quiz_end_title_gold);
        else if (mCorrectPct >= .85f)
            return res.getString(R.string.quiz_end_title_silver);
        else if (mCorrectPct >= .70f)
            return res.getString(R.string.quiz_end_title_bronze);
        else
            return res.getString(R.string.quiz_end_title_lose);

    }

    public String getSubtitle()
    {
        Resources res = AppPinPin.getAppResources();

        if (mCorrectPct == 1.0f)
            return res.getString(R.string.quiz_end_subtitle_gold);
        else if (mCorrectPct >= .85f)
            return res.getString(R.string.quiz_end_subtitle_silver);
        else if (mCorrectPct >= .70f)
            return res.getString(R.string.quiz_end_subtitle_bronze);
        else
            return res.getString(R.string.quiz_end_subtitle_lose);
    }

    public int getTrophyResourceId()
    {
        if (mCorrectPct == 1.0f)
            return R.drawable.trophy_gold_large;
        else if (mCorrectPct >= .85f)
            return R.drawable.trophy_silver_large;
        else if (mCorrectPct >= .70f)
            return R.drawable.trophy_bronze_large;
        else
            return R.drawable.trophy_placeholder_large;
    }

    public String getCorrectPctText()
    {
        return String.format("%.01f", mCorrectPct * 100.0f);
    }

    public String getIncorrectPctText()
    {
        return String.format("%.01f", mIncorrectPct * 100.0f);
    }
}
