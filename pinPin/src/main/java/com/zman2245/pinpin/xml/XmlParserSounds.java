package com.zman2245.pinpin.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/*
 * Parser for the Sounds Plist file from the iOS PinPin app
 */
public class XmlParserSounds
{
    // We don't use namespaces
    private static final String ns = null;

    public HashMap<String, Object> parse(InputStream in) throws XmlPullParserException, IOException
    {
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readSlideshow(parser);
        }
        finally
        {
            in.close();
        }
    }

    private HashMap<String, Object> readSlideshow(XmlPullParser parser) throws XmlPullParserException, IOException
    {
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	String key = null;
    	Object obj = null;

        parser.require(XmlPullParser.START_TAG, ns, "dict");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String name = parser.getName();

            // Looks for 'key' tags
            if (name.equals("key"))
            	key = readText(parser);
            else if (name.equals("dict"))
            	obj = readSlideshow(parser);
            else if (name.equals("array"))
            	obj = readArray(parser);
            else if (name.equals("string"))
            	obj = readText(parser);

            if (key == null)
            	throw new IllegalStateException("obj found but no key");
            
            map.put(key, obj);
        }

        return map;
    }

    private String[] readArray(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        ArrayList<String> list = new ArrayList<String>();

        parser.require(XmlPullParser.START_TAG, ns, "array");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("string"))
                list.add(readText(parser));
        }

        Object[] objectArray = list.toArray();
        
        return Arrays.copyOf(objectArray, objectArray.length, String[].class);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
