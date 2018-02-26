package com.zman2245.pinpin.util.content;

import android.content.SharedPreferences.Editor;
import android.os.Build;

/**
 * Contains static methods for help with apis that have compat issues
 * 
 * @author zack
 */
public class CompatUtil
{
    /**
     * SharedPreferences Editor apply is better because it is async, but it is
     * only available starting at API level 9.
     * 
     * @param editor
     */
    public static void editorApply(Editor editor)
    {
        // hack to support api level 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            editor.apply();
        else
            editor.commit();
    }
}
