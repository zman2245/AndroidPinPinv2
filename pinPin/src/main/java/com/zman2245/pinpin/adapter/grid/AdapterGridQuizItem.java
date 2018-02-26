package com.zman2245.pinpin.adapter.grid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zman2245.pinpin.R;
import com.zman2245.pinpin.view.listitem.ViewQuizChoice;

/**
 * An adapter for a quiz question choices
 *
 * Has the ability to enable/disable a column of items
 *
 * @author zack
 */
public class AdapterGridQuizItem extends BaseAdapter
{
    private final LayoutInflater mInflater;
    private String[][] mWords;
    private final boolean[] mColumnEnabled;
    private int mNumRows;
    private int mNumCols;

    /**
     * Constructor
     *
     * @param words Must not be null. All rows must have the same number of columns and must not be null.
     * @param inflater Must not be null
     */
    public AdapterGridQuizItem(String[][] words, LayoutInflater inflater)
    {
        if (inflater == null || words == null || words[0] == null)
            throw new IllegalArgumentException("words and inflator must be non-null");

        mInflater = inflater;

        setWords(words);
        mColumnEnabled = new boolean[mNumCols];
        for (int i = 0; i < mNumCols; i++)
        {
        	mColumnEnabled[i] = true;
        }
    }

    /**
     * Set the content
     *
     * @param words  The data behind the presentation
     */
    public void setWords(String[][] words)
    {
        mWords       = words;
        String[] row = mWords[0];
        mNumRows     = row.length;
        mNumCols     = mWords.length;

        notifyDataSetChanged();
    }

    public void setColumnEnabled(int index, boolean enabled)
    {
    	mColumnEnabled[index] = enabled;
    	notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mNumRows * mNumCols;
    }

    @Override
    public Object getItem(int position)
    {
        int row = position % mNumCols;
        int col = position / mNumCols;

        return mWords[row][col];
    }

    @Override
    public long getItemId(int position)
    {
        // all items are the same
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.item_grid_quiz_choice, parent, false);

        ViewQuizChoice view = (ViewQuizChoice)convertView;

        view.setText((String)getItem(position));

        return convertView;
    }

    @Override
    public boolean isEnabled(int position)
    {
        int column = position % mNumCols;

        return mColumnEnabled[column];
    }
}
