package com.zman2245.pinpin.util.content;

import java.util.ArrayList;

import com.zman2245.pinpin.data.DataItemLearnFlow;
import com.zman2245.pinpin.data.DataItemPractice;

/**
 * Util for building content for the practice screen
 * 
 * @author Zack
 */
public class UtilPracticeContent
{
    /**
     * Derive practice data from an array of learn data
     * 
     * @param learnDatas
     * @return
     */
    public static DataItemPractice[] getPracticeDataFromLearnData(DataItemLearnFlow[] learnDatas)
    {
        ArrayList<DataItemPractice> array = new ArrayList<DataItemPractice>();
        for (DataItemLearnFlow learnData : learnDatas)
        {
            if (learnData.syllables == null)
                continue;

            for (String[] row : learnData.syllables)
            {
                for (String word : row)
                {
                    if (word == null || "".equals(word))
                        continue;

                    DataItemPractice item = new DataItemPractice();
                    item.word = word;
                    array.add(item);
                }
            }
        }

        return array.toArray(new DataItemPractice[array.size()]);
    }
}
