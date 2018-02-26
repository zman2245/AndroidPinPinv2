package com.zman2245.pinpin.util.audio;

import java.util.HashMap;

import com.zman2245.pinpin.AppPinPin;

/**
 * Mapping implementation using a static hash map
 *
 * @author zack
 */
public class AudioResourceMapperImpl implements AudioResourceMapper
{
    private static final HashMap<String, AudioMapping> WORD_MAP = createWordMap();

    private static HashMap<String, AudioMapping> createWordMap()
    {
        HashMap<String, AudioMapping> wordMap = new HashMap<String, AudioMapping>();

        // First Tones
        wordMap.put("\u0101", new AudioMapping("a", "_1"));
        wordMap.put("\u0113", new AudioMapping("e", "_1"));
        wordMap.put("\u012B", new AudioMapping("i", "_1"));
        wordMap.put("\u014D", new AudioMapping("o", "_1"));
        wordMap.put("\u016B", new AudioMapping("u", "_1"));
        wordMap.put("\u01D6", new AudioMapping("_umlaut", "_1"));
        wordMap.put("u\u0308\u0304", new AudioMapping("_umlaut", "_1"));

        // Second Tones
        wordMap.put("\u00E1", new AudioMapping("a", "_2"));
        wordMap.put("\u00E9", new AudioMapping("e", "_2"));
        wordMap.put("\u00ED", new AudioMapping("i", "_2"));
        wordMap.put("\u00F3", new AudioMapping("o", "_2"));
        wordMap.put("\u00FA", new AudioMapping("u", "_2"));
        wordMap.put("\u01D8", new AudioMapping("_umlaut", "_2"));
        wordMap.put("u\u0308\u0301", new AudioMapping("_umlaut", "_2"));

        // Third Tones
        wordMap.put("\u01CE", new AudioMapping("a", "_3"));
        wordMap.put("\u011B", new AudioMapping("e", "_3"));
        wordMap.put("\u01D0", new AudioMapping("i", "_3"));
        wordMap.put("\u01D2", new AudioMapping("o", "_3"));
        wordMap.put("\u01D4", new AudioMapping("u", "_3"));
        wordMap.put("\u01DA", new AudioMapping("_umlaut", "_3"));
        wordMap.put("u\u0308\u030C", new AudioMapping("_umlaut", "_3"));

        // Fourth Tones
        wordMap.put("\u00E0", new AudioMapping("a", "_4"));
        wordMap.put("\u00E8", new AudioMapping("e", "_4"));
        wordMap.put("\u00EC", new AudioMapping("i", "_4"));
        wordMap.put("\u00F2", new AudioMapping("o", "_4"));
        wordMap.put("\u00F9", new AudioMapping("u", "_4"));
        wordMap.put("\u01DC", new AudioMapping("_umlaut", "_4"));
        wordMap.put("u\u0308\u0300", new AudioMapping("_umlaut", "_1"));

        return wordMap;
    }

    @Override
    public int getResourceForString(String word)
    {
        int resource = -1;

        for (String key : WORD_MAP.keySet())
        {
            if (word.contains(key))
            {
                AudioMapping mapping    = WORD_MAP.get(key);
                String resName          = word.replace(key, mapping.replacement) + mapping.suffix;

                resource = AppPinPin.getAppResources().getIdentifier(resName, "raw", AppPinPin.getInstance().getPackageName());

                if (resource <= 0)
                    throw new IllegalArgumentException("Tried to map the input but could not find a resource. Word: " + word + ". Mapped resource name: " + resName);

                return resource;
            }
        }

        throw new IllegalArgumentException("No audio resource or mapping found for " + word);
    }

    /**
     * Contains information for mapping a string to an audio resource filename
     *
     * @author zack
     */
    private static class AudioMapping
    {
        public String replacement;
        public String suffix;

        public AudioMapping(String replacement, String suffix)
        {
            this.replacement    = replacement;
            this.suffix         = suffix;
        }
    }
}
