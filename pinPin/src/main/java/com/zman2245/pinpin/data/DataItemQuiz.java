package com.zman2245.pinpin.data;

import java.io.Serializable;

/**
 * Data to be presented for a quiz question
 *
 * Note that one question may have multipe parts
 *
 * @author zack
 */
public class DataItemQuiz implements Serializable
{
    private static final long serialVersionUID = 1L;

    public String quiz_id;

    public String answers[]		= {};
    public String[][] choices   = {};

    // Tells whether this question already completed
    public boolean alreadyComplete = false;
}
