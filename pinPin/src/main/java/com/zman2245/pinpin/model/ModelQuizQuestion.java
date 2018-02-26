package com.zman2245.pinpin.model;

import java.util.Arrays;

import com.zman2245.pinpin.data.DataItemQuiz;


/**
 * Quiz question model
 *
 * @author Zack
 */
public class ModelQuizQuestion
{
	public enum QuizQuestionState
	{
		UNANSWERED, CORRECT, INCORRECT
	}

	private final DataItemQuiz mData;
	private final QuizQuestionState[] mStates;
	private final boolean[] mCorrects;
	private final String[] mAnswers;

	/**
	 * Returns a new array of states initialized to all "UNANSWERED"
	 *
	 * @param len
	 * @return
	 */
	public static QuizQuestionState[] getNewArray(int len)
	{
		QuizQuestionState[] states = new QuizQuestionState[len];

		for (int i = 0; i < len; i++)
		{
			states[i] = QuizQuestionState.UNANSWERED;
		}

		return states;
	}

	public ModelQuizQuestion(DataItemQuiz data)
	{
		mData		= data;
		mAnswers 	= data.answers;
		mStates 	= getNewArray(mAnswers.length);
		mCorrects	= new boolean[mAnswers.length];
	}

	public QuizQuestionState[] getStates()
	{
		return mStates;
	}

	public QuizQuestionState getStateAt(int index)
	{
		return mStates[index];
	}

	public String getAnswerText()
	{
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < mAnswers.length; i++)
		{
			if (mCorrects[i] || mData.alreadyComplete)
			{
				builder.append(mAnswers[i]);
			}
			else
			{
				// put in an empty placeholder for the unanswered
				char[] charArray = new char[mAnswers[i].length()];
				Arrays.fill(charArray,' ');
				builder.append(charArray);
			}

			if (i < mAnswers.length - 1)
				builder.append(" ");
		}

		return builder.toString();
	}


	/**
	 * Check the validity of a guess
	 *
	 * @param guess
	 * @return true if the guess given is correct; otherwise false
	 */
	public boolean answer(String guess, int index)
	{
		boolean isCorrect = mAnswers[index].equals(guess);

		if (mStates[index] == QuizQuestionState.UNANSWERED)
		{
			mStates[index] = isCorrect ? QuizQuestionState.CORRECT : QuizQuestionState.INCORRECT;
		}

		mCorrects[index] = isCorrect;

		return isCorrect;
	}

	/**
	 * Returns true if this question was already completed
	 */
	public boolean isAlreadyComplete()
	{
		return mData.alreadyComplete;
	}
}
