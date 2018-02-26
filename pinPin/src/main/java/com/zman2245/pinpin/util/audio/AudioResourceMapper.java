package com.zman2245.pinpin.util.audio;

/**
 * Helps find an audio resource
 *
 * @author zack
 */
public interface AudioResourceMapper
{
    /**
     * Maps a string to an audio resource
     *
     * @param word
     * @return  -1 if no mapping was found
     */
    int getResourceForString(String word);
}
