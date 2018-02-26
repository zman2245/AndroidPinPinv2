package com.zman2245.pinpin.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.Registry;
import com.zman2245.pinpin.data.DataItemProgress;

/**
 * ListView adapter for the main Quiz section
 *
 * @author zack
 */
public class AdapterListQuiz extends BaseAdapter
{
    private final LayoutInflater mInflater;

    private final String[] mTitles;
    private final String[] mQuizIds;

    public AdapterListQuiz(Context context, LayoutInflater inflater)
    {
        mInflater = inflater;
        mTitles   = context.getResources().getStringArray(R.array.quiz_list_titles);

        mQuizIds = new String[mTitles.length];
        for (int i = 0; i < mTitles.length; i++)
        {
            mQuizIds[i] = AppPinPin.sQuizGenerator.getQuizQuestions(i)[0].quiz_id;
        }
    }

    @Override
    public int getCount()
    {
        return mTitles.length;
    }

    @Override
    public Object getItem(int position)
    {
        // not needed
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // All are the same
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView         = mInflater.inflate(R.layout.item_list_quiz, parent, false);
            holder              = new ViewHolder();
            holder.mTitle       = (TextView) convertView.findViewById(R.id.title);
            holder.mImage       = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        DataItemProgress prog = Registry.sProgressFactory.getQuizProgress(mQuizIds[position]);

        holder.mImage.setImageResource(Registry.sProgressFactory.getTrophyResource(prog));
        holder.mTitle.setText(mTitles[position]);

        return convertView;
    }

    /**
     * View Holder class
     *
     * @author zfoster
     */
    private static class ViewHolder
    {
        TextView  mTitle;
        ImageView mImage;
    }
}
