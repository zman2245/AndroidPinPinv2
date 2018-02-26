package com.zman2245.pinpin.appstate;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemProgress;
import com.zman2245.pinpin.util.content.CompatUtil;

/**
 * Handles saving/restoring application state
 *
 * For example, progress statuses for quiz and learn sections
 *
 * @author zfoster
 */
@SuppressLint("CommitPrefEdits")
public class ProgressFactory
{
    private final SharedPreferences mPrefs;

    private InAppPurchasesModel mPurchasesModel;

    private final HashMap<String, DataItemProgress> mLearnProgresses;
    private final HashMap<String, DataItemProgress> mQuizProgresses;

    public ProgressFactory(Context context, SharedPreferences prefs)
    {
        mPrefs = prefs;

        mLearnProgresses = new HashMap<String, DataItemProgress>();
        mQuizProgresses = new HashMap<String, DataItemProgress>();

        // build learn dataset
        String[] learnIds = context.getResources().getStringArray(R.array.learn_ids);
        for (String id : learnIds)
        {
            String jsonEncodedData = prefs.getString(id, "");
            DataItemProgress data = fromJson(jsonEncodedData);
            mLearnProgresses.put(id, data);
        }

        // build quiz dataset
        String[] quizIds = context.getResources().getStringArray(R.array.quiz_ids);
        for (String id : quizIds)
        {
            String jsonEncodedData = prefs.getString(id, "");
            DataItemProgress data  = fromJson(jsonEncodedData);
            mQuizProgresses.put(id, data);
        }
    }

    /**
     * Mark progress for a quiz section
     *
     * @param title
     * @param lastItem
     * @param totalItems
     * @param score
     */
    public void markQuizProgress(String title, int lastItem, int totalItems, float score)
    {
        DataItemProgress data = mQuizProgresses.get(title);

        if (data == null)
            throw new IllegalArgumentException("title (" + title + ") not found in the quiz progrss map");

        if (lastItem > data.last_item_completed || (lastItem == (totalItems - 1) && score >= data.score))
        {
            Editor editor = mPrefs.edit();

            data.last_item_completed    = lastItem;
            data.total_items            = totalItems;
            data.completed              = (lastItem >= (totalItems - 1));
            data.score                  = score;

            editor.putString(title, toJsonString(data));
            CompatUtil.editorApply(editor);
        }
    }

    /**
     * Get the progress for a quiz section
     *
     * @param title
     * @return
     */
    public DataItemProgress getQuizProgress(String title)
    {
        DataItemProgress data = mQuizProgresses.get(title);

        if (data == null)
            throw new IllegalArgumentException("title (" + title + ") not found in the quiz progrss map");

        return data;
    }

    /**
     * Mark progress for a lession (i.e. Learn section)
     *
     * Note: the records are indexed by the section title
     *
     * @param title
     * @param lastItem
     * @param totalItems
     */
    public void markLearnProgress(String title, int lastItem, int totalItems)
    {
        DataItemProgress data = mLearnProgresses.get(title);

        if (data == null)
            throw new IllegalArgumentException("title (" + title + ") not found in the learn progress map.");

        if (lastItem > data.last_item_completed)
        {
            Editor editor = mPrefs.edit();

            data.last_item_completed = lastItem;
            data.total_items = totalItems;
            data.completed = (lastItem >= (totalItems - 1));

            editor.putString(title, toJsonString(data));
            CompatUtil.editorApply(editor);
        }
    }

    /**
     * Get the progress for a lession (i.e. Learn section)
     *
     * Note: the records are indexed by the section title
     *
     * @param title
     * @return
     */
    public DataItemProgress getLearnProgress(String title)
    {
        DataItemProgress data = mLearnProgresses.get(title);

        if (data == null)
            throw new IllegalArgumentException("title (" + title + ") not found in the learn progress map.");

        return data;
    }

    /**
     * Provides the proper trophy to use given a progress
     *
     * @param prog
     * @return
     */
    public int getTrophyResource(DataItemProgress prog)
    {
        if (prog.completed && prog.score >= 1.0f) // gold
            return R.drawable.trophy_gold;
        else if (prog.completed && prog.score >= .85f) // silver
            return R.drawable.trophy_silver;
        else if (prog.completed && prog.score >= .70f) // bronze
            return R.drawable.trophy_bronze;
        else // not completed or too low of a score, just display placeholder
            return R.drawable.trophy_placeholder;
    }

    // private helpers

    private String toJsonString(DataItemProgress item)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put("completed", item.completed);
            json.put("last_item_completed", item.last_item_completed);
            json.put("total_items", item.total_items);
            json.put("score", item.score);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }

    private DataItemProgress fromJson(String jsonString)
    {
        DataItemProgress data = new DataItemProgress();

        if (TextUtils.isEmpty(jsonString))
            return data;

        try
        {
            JSONObject json = new JSONObject(jsonString);

            data.completed = json.getBoolean("completed");
            data.last_item_completed = json.getInt("last_item_completed");
            data.total_items = json.getInt("total_items");
            data.score = (float)json.getDouble("score");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return data;
    }
}
