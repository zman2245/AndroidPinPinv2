package com.zman2245.pinpin.adapter.grid;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zman2245.pinpin.R;

/**
 * An adapter for a grid of pinyin syllables
 *
 * @author zack
 */
public class AdapterGridReference extends BaseAdapter
{
    private final LayoutInflater mInflater;
    private String[][] mWords;
    private int mNumRows;
    private int mNumCols;

    /**
     * Constructor
     *
     * @param words Must not be null. All rows must have the same number of columns and must not be null.
     * @param inflater Must not be null
     */
    public AdapterGridReference(String[][] words, LayoutInflater inflater)
    {
        if (inflater == null || words == null || words[0] == null)
            throw new IllegalArgumentException("words and inflator must be non-null");

        mInflater = inflater;

        setWords(words);
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
        mNumCols     = row.length;
        mNumRows     = mWords.length;

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
        int row = position / mNumCols;
        int col = position % mNumCols;

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
            convertView = mInflater.inflate(R.layout.item_grid_reference, parent, false);

        TextView textView = (TextView) convertView;

        textView.setText((String)getItem(position));

        return convertView;
    }

    @Override
    public boolean isEnabled(int position)
    {
        Object item = getItem(position);
        if (item == null || "".equals(item))
            return false;

        return true;
    }
}
