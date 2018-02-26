package com.zman2245.pinpin.data;

import java.io.Serializable;

/**
 * Data to be presented at the end of a quiz
 *
 * @author zack
 */
public class DataItemEndQuiz implements Serializable
{
    private static final long serialVersionUID = 1L;

    public float correctPct;
    public float incorrectPct;
}
