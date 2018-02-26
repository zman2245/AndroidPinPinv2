package com.zman2245.pinpin.util.audio;

import com.zman2245.pinpin.R;

/**
 * Pinyin Tones
 *
 * @author zack
 */
public enum Tone
{
    FIRST(0, "_1", R.array.reference_first, R.drawable.first_tone_white),
    SECOND(1, "_2", R.array.reference_second, R.drawable.second_tone_white),
    THIRD(2, "_3", R.array.reference_third, R.drawable.third_tone_white),
    FOURTH(3, "_4", R.array.reference_fourth, R.drawable.fourth_tone_white);

    public int index;
    public String fileExt;
    public int refResId;
    public int refWhiteIcon;

    private Tone(int index, String ext, int refResId, int refWhiteIcon)
    {
    	this.index         = index;
    	this.fileExt       = ext;
        this.refResId      = refResId;
        this.refWhiteIcon  = refWhiteIcon;
    }
}
