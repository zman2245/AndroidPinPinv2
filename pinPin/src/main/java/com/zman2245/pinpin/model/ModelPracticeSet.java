package com.zman2245.pinpin.model;

import com.zman2245.pinpin.data.DataItemPractice;

/**
 * Model for a practice set
 *
 * @author Zack
 */
public class ModelPracticeSet
{
	private final DataItemPractice[] mDatas;
	private int mCurrentPractice;

	public ModelPracticeSet(DataItemPractice[] datas)
	{
	    mDatas = datas;
	}

	/**
	 * Increments the current question pointer and returns the
	 * quiz question data at the new pointer
	 *
	 * @return
	 */
	public DataItemPractice next()
	{
	    mCurrentPractice++;

		return getCurrentData();
	}

	/**
	 * Decrements the current question pointer and returns the
	 * quiz question data at the new pointer
	 *
	 * @return
	 */
	public DataItemPractice previous()
	{
	    mCurrentPractice--;

		return getCurrentData();
	}

	/**
	 * Gives the data for the current quiz question
	 *
	 * The current pointer is not changed
	 *
	 * @return
	 */
	public DataItemPractice peek()
	{
		return getCurrentData();
	}

	/**
	 * Helper used by various data-fetching methods
	 *
	 * @return
	 */
	private DataItemPractice getCurrentData()
	{
		if (mDatas == null || mCurrentPractice < 0 || mCurrentPractice >= mDatas.length)
			return null;

		return mDatas[mCurrentPractice];
	}
}
