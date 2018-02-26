package com.zman2245.pinpin.util.content;

import java.util.HashMap;
import java.util.Random;

import android.content.res.TypedArray;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemQuiz;
import com.zman2245.pinpin.util.audio.Tone;

/**
 * Generates quiz questions from resources
 *
 * Following things are randomized:
 *   1.) Order of questions within a quiz
 *   2.) Order of answers
 *   3.) If "_random" is specified as the answer, the answer is selected
 *       randomly from the generated choices
 *
 * Other conventions:
 * 	 "|" is used to specifies possibilities
 *   "&" is used for denoting more than one slot (aka sub-question)
 *
 * @author Zack
 */
public class UtilQuizGenerator
{
	private static int TYPE_SINGLE_TONE = 0;
	private static int TYPE_MULTI_TONE 	= 1;

	private static int INDEX_TYPE 		= 0;
	private static int INDEX_ANSWERS 	= 1;
	private static int INDEX_CHOICES 	= 2;
	private static int INDEX_TONES 		= 3;
	private static int INDEX_WORD 		= 4;

	private static String SPLIT 		= "\\|";
	private static String UNION			= "\\+";

	public DataItemQuiz[] getQuizQuestions(int index)
	{
		int resId;
		TypedArray quizArray;

		// Get the master array that holds all quiz information
		TypedArray masterArray = AppPinPin.getTypedArray(R.array.quiz_questions_master);
		if (index >= masterArray.length() || index < 0)
		{
			masterArray.recycle();
			throw new IndexOutOfBoundsException("Index is out of range");
		}

		// Get the quiz array for the given index
		resId = masterArray.getResourceId(index, 0);
		quizArray = AppPinPin.getTypedArray(resId);

		// Get the identifier of the quiz at this index
		String quizId = AppPinPin.getStringArray(R.array.quiz_ids)[index];

		DataItemQuiz[] items = new DataItemQuiz[quizArray.length()];
		for (int i = 0; i < quizArray.length(); i++)
		{
			resId = quizArray.getResourceId(i, -1);
			String[] questionArray = quizArray.getResources().getStringArray(resId);

			items[i]         = getQuizQuestion(questionArray);
			items[i].quiz_id = quizId;
		}

		// Always recycle! TypedArrays are cached and re-used so
		// you got to recycle them
		masterArray.recycle();
		quizArray.recycle();

		// randomize the questions for this quiz
		shuffle(items);

		return items;
	}

	private DataItemQuiz getQuizQuestion(String[] questionArray)
	{
		String[] types = null;
		String[] answers = null;
		String[] choices = null;
		String[] tones = null;
		String[] words = null; /* Currently unused, but it is included in iOS data files */
		DataItemQuiz question = new DataItemQuiz();
		int numSubQuestions = 1;

		// Break up by sub-questions
		types  	   = questionArray[INDEX_TYPE].split(UNION);
		answers    = questionArray[INDEX_ANSWERS].split(UNION);
		if (questionArray.length > 2)
			choices = questionArray[INDEX_CHOICES].split(UNION);
		if (questionArray.length > 3)
			tones = questionArray[INDEX_TONES].split(UNION);
		if (questionArray.length > 4)
			words = questionArray[INDEX_WORD].split(UNION);

		numSubQuestions = answers.length;

		// ensure all are of the same length
		if (answers.length != types.length)
			throw new IllegalArgumentException("mismatch in number of sub questions: answers length = " + answers.length + ". choices length = " + choices.length);

		// allocate stuff for the question
		question.answers = new String[numSubQuestions];
		question.choices = new String[numSubQuestions][];

		for (int i = 0; i < numSubQuestions; i++)
		{
			// differentiate by question type  COULD BE REFACTORED!
			if (Integer.valueOf(types[i]) == TYPE_SINGLE_TONE)
			{
				String[] toneStringArray 	= (tones == null ? null : tones[i].split(SPLIT));
				Tone tone 					= getRandomTone(toneStringArray);
				String answer			 	= getRandomAnswer(answers[i].split(SPLIT), tone);
				String[] choice 			= getChoicesFromTone(choices[i].split(SPLIT), tone);

				question.answers[i] = answer;
				question.choices[i] = choice;
			}
			else if (Integer.valueOf(types[i]) == TYPE_MULTI_TONE)
			{
				Tone tone = getRandomTone(null);

				HashMap<String, Object> map = (HashMap<String, Object>)AppPinPin.sSoundMap.get(answers[i]);
				String[] titles 			= (String[])map.get("title");

				question.answers[i] = titles[tone.index];
				question.choices[i] = titles;
			}

			// randomize the choices
			shuffle(question.choices[i]);
		}

		return question;
	}

	/**
	 * Given an array of tones represented by Strings ("1", "2", "3", "4"),
	 * return a Tone based on a random selection from the array
	 *
	 * If null is input, a Tone will be selected from the set of all tones
	 *
	 * @param tones
	 * @return
	 */
	private Tone getRandomTone(String[] tones)
	{
		String[] allTones = {"1","2","3","4"};
		if (tones == null || tones.length == 0)
			tones = allTones;

		int index = (int)Math.floor(Math.random() * tones.length);
		String tone = tones[index];

		if (tone.equals("1"))
			return Tone.FIRST;
		else if (tone.equals("2"))
			return Tone.SECOND;
		else if (tone.equals("3"))
			return Tone.THIRD;
		else
			return Tone.FOURTH;
	}

	/**
	 * Select an answer randomly from the given set. The final answer will have a Tone
	 * as specified by tone
	 *
	 * @param answers The set of answers to choose from
	 * @param tone The Tone of the output answer
	 * @return An answer
	 */
	private String getRandomAnswer(String[] answers, Tone tone)
	{
		String answer;
		int index = (int)Math.floor(Math.random() * answers.length);
		answer = answers[index];

		HashMap<String, Object> map = (HashMap<String, Object>)AppPinPin.sSoundMap.get(answer);
		String[] titles 			= (String[])map.get("title");

		answer = titles[tone.index];

		return answer;
	}

	/**
	 * Map the base choices to real pinyin choices that have a tone
	 *
	 * @param choices
	 * @param tone
	 * @return
	 */
	private String[] getChoicesFromTone(String[] choices, Tone tone)
	{
		String[] mappedChoices = new String[choices.length];

		for (int i = 0; i < choices.length; i++)
		{
			HashMap<String, Object> map = (HashMap<String, Object>)AppPinPin.sSoundMap.get(choices[i]);
			String[] titles 			= (String[])map.get("title");

			mappedChoices[i] = titles[tone.index];
		}

		return mappedChoices;
	}

	/**
	 * Shuffle an array of objects by exchanging each element randomly
	 *
	 * Note: this method modifies the input array
	 *
	 * @param objects
	 */
	private void shuffle(Object[] objects)
	{
		Random rgen = new Random();

		//--- Shuffle by exchanging each element randomly
		for (int i=0; i < objects.length; i++)
		{
			int randomPosition = rgen.nextInt(objects.length);
			Object temp = objects[i];
			objects[i] = objects[randomPosition];
			objects[randomPosition] = temp;
		}
	}
}
