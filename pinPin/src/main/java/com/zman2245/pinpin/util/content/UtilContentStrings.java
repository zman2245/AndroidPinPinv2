package com.zman2245.pinpin.util.content;

import android.content.res.TypedArray;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemLearnFlow;
import com.zman2245.pinpin.util.audio.Tone;


/**
 * Contains static helper methods for accessing content from string resources
 *
 * @author zack
 */
public class UtilContentStrings
{
    private static final int INDEX_TOP_STRINGS      = 0;
    private static final int INDEX_BOTTOM_STRINGS   = 1;
    private static final int INDEX_BUTTON_STRINGS   = 2;

    /**
     * Return the content for a "learn" section
     *
     * @param pos
     * @return
     */
    public static DataItemLearnFlow[] getLearnSectionData(int pos)
    {
        long ts = System.currentTimeMillis();

        String[] topStrings;
        String[] bottomStrings;
        TypedArray arraysLearnContent;
        TypedArray sectionTa;
        TypedArray sectionButtons = null;

        arraysLearnContent = AppPinPin.getTypedArray(R.array.arrays_learn_content);
        int resId = arraysLearnContent.getResourceId(pos, 0);
        sectionTa = AppPinPin.getTypedArray(resId);

        // Get the section id
        String sectionId = AppPinPin.getStringArray(R.array.learn_ids)[pos];

        // Get top strings array
        resId = sectionTa.getResourceId(INDEX_TOP_STRINGS, 0);
        topStrings = arraysLearnContent.getResources().getStringArray(resId);

        // Get bottom strings array
        resId = sectionTa.getResourceId(INDEX_BOTTOM_STRINGS, 0);
        bottomStrings = arraysLearnContent.getResources().getStringArray(resId);

        if (sectionTa.length() > 2)
        {
            resId = sectionTa.getResourceId(INDEX_BUTTON_STRINGS, 0);
            sectionButtons = AppPinPin.getTypedArray(resId);
        }

        int len = topStrings.length;
        DataItemLearnFlow[] datas = new DataItemLearnFlow[len];
        for (int i = 0; i < len; i++)
        {
            DataItemLearnFlow data = new DataItemLearnFlow();

            data.section_id = sectionId;
            data.topText    = topStrings[i];
            data.bottomText = bottomStrings[i];
            data.syllables  = getButtonsMatrix(sectionButtons, i);

            datas[i] = data;
        }

        datas[0].title = AppPinPin.getStringArray(R.array.strings_learn_list_titles)[pos];

        // Important to recycle!
        arraysLearnContent.recycle();
        sectionTa.recycle();

        if (sectionButtons != null)
            sectionButtons.recycle();

        return datas;
    }

    private static String[][] getButtonsMatrix(TypedArray sectionButtons, int pos)
    {
        if (sectionButtons == null)
            return null;

        int resId = sectionButtons.getResourceId(pos, 0);

        // there may be no buttons at this index
        if (resId == 0)
            return null;

        TypedArray pageButtons  = AppPinPin.getTypedArray(resId);

        int numRows         = pageButtons.length();
        int lenOfFirstRow   = 0;
        String[][] val      = new String[numRows][];

        for (int i = 0; i < numRows; i++)
        {
            resId   = pageButtons.getResourceId(i, 0);
            val[i]  = pageButtons.getResources().getStringArray(resId);

            // make sure all rows have the same length
            if (i == 0)
                lenOfFirstRow = val[i].length;
            if (val[i].length != lenOfFirstRow)
                throw new IllegalArgumentException("Length of all rows must be the same! Length of row 0: " + lenOfFirstRow + ". Length of row " + i + ": " + val[i].length);
        }

        // Important to recycle!
        pageButtons.recycle();

        return val;
    }

    /**
     * Return the content for the reference grid
     */
    public static String[][] getReferenceStrings(Tone tone)
    {
        int resId;
        TypedArray contentArray = AppPinPin.getTypedArray(tone.refResId);
        String[][] strings      = new String[contentArray.length()][];

        for (int i = 0; i < contentArray.length(); i++)
        {
            resId       = contentArray.getResourceId(i, 0);
            strings[i]  = contentArray.getResources().getStringArray(resId);
        }

        return strings;
    }

    /**
     * Return the content for the reference grid top bar
     */
    public static String[] getReferenceTopbarStrings()
    {
        return AppPinPin.getStringArray(R.array.reference_topbar);
    }
}
