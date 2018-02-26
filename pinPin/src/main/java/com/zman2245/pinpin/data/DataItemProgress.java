package com.zman2245.pinpin.data;

/**
 * An object that contains information about progress made in a section (could
 * be a learn section or quiz section, for example)
 *
 * @author zfoster
 *
 */
public class DataItemProgress
{
    public boolean completed    = false;
    public int     last_item_completed = -1;
    public int     total_items = 0;

    // used by quiz only right now
    public float score = 0;
}
