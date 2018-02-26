package com.zman2245.pinpin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Application;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.zman2245.pinpin.appstate.ProgressFactory;
import com.zman2245.pinpin.log.EventLog;
import com.zman2245.pinpin.util.audio.AudioResourceMapper;
import com.zman2245.pinpin.util.audio.AudioResourceMapperImpl;
import com.zman2245.pinpin.util.content.UtilQuizGenerator;
import com.zman2245.pinpin.xml.XmlParserSounds;

/**
 * The Pin Pin application!
 *
 * @author zack
 */
public class AppPinPin extends Application
{
    private static AppPinPin sInstance;
    private final AudioResourceMapper mAudioMapper;
    public static HashMap<String, Object> sSoundMap;
    public static HashMap<String, String> sSoundMapReverse; // maps each sound to a parent, non-toned sound that can then be looked up in sSoundMap
    public static UtilQuizGenerator sQuizGenerator;

    public AppPinPin()
    {
        super();

        sInstance = this;
        Registry.sApp = this;
        mAudioMapper = new AudioResourceMapperImpl();
        sQuizGenerator = new UtilQuizGenerator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate()
    {
        super.onCreate();

        Registry.sProgressFactory = new ProgressFactory(this, getSharedPreferences("appstate", 0));

        dumpDeviceInfo();

        if (AppPinPin.sSoundMap == null)
        {
            rebuildSoundMap();
        }

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.flurry_api_key));

        EventLog.init(this);
    }

    // for testing only
    public static void rebuildSoundMap()
    {
        try
        {
            InputStream is = AppPinPin.sInstance.getAssets().open("sounds.xml");
            XmlParserSounds parser = new XmlParserSounds();
            long ts = System.currentTimeMillis();
            HashMap<String, Object> soundsMap = parser.parse(is);
            AppPinPin.sSoundMap = soundsMap;

            // manually put in umlauts
            // TODO: there are some issues in putting these characters in the sounds.xml and parsing
            // maybe something to do with the format of the sounds.xml file itself?
            HashMap<String, Object> innerMap = new HashMap<String, Object>();
            String[] titles = new String[4];
            titles[0] = "nǖ";
            titles[1] = "nǘ";
            titles[2] = "nǚ";
            titles[3] = "nǜ";
            innerMap.put("title", titles);
            soundsMap.put("n&#252;", innerMap);
            innerMap = new HashMap<String, Object>();
            titles = new String[4];
            titles[0] = "nǖe";
            titles[1] = "nǘe";
            titles[2] = "nǚe";
            titles[3] = "nǜe";
            innerMap.put("title", titles);
            soundsMap.put("n&#252;e", innerMap);
            innerMap = new HashMap<String, Object>();
            titles = new String[4];
            titles[0] = "lǖ";
            titles[1] = "lǘ";
            titles[2] = "lǚ";
            titles[3] = "lǜ";
            innerMap.put("title", titles);
            soundsMap.put("l&#252;", innerMap);
            innerMap = new HashMap<String, Object>();
            titles = new String[4];
            titles[0] = "lǖe";
            titles[1] = "lǘe";
            titles[2] = "lǚe";
            titles[3] = "lǜe";
            innerMap.put("title", titles);
            soundsMap.put("l&#252;e", innerMap);

            buildReverseSoundMap();
            Log.d("DEBUG", "rebuilding time took in milliseconds: " + (System.currentTimeMillis() - ts));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static AppPinPin getInstance()
    {
        return sInstance;
    }

    public static Resources getAppResources()
    {
        return sInstance.getResources();
    }

    public static String[] getStringArray(int id)
    {
        return sInstance.getResources().getStringArray(id);
    }

    public static TypedArray getTypedArray(int id)
    {
        return sInstance.getResources().obtainTypedArray(id);
    }

    public static AudioResourceMapper getAudioMapper()
    {
        return sInstance.mAudioMapper;
    }

    private void dumpDeviceInfo()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        String density = "";

        switch (metrics.densityDpi)
        {
        case DisplayMetrics.DENSITY_HIGH:
            density = "hdpi";
            break;
        case DisplayMetrics.DENSITY_LOW:
            density = "ldpi";
            break;
        case DisplayMetrics.DENSITY_MEDIUM:
            density = "mdpi";
            break;
        case DisplayMetrics.DENSITY_TV:
            density = "tvdpi";
            break;
        case DisplayMetrics.DENSITY_XHIGH:
            density = "xhdpi";
            break;
        case DisplayMetrics.DENSITY_XXHIGH:
            density = "xxhdpi";
            break;
        }

        Log.d("DeviceInfo", "density = " + density + ". dimensions = " + metrics.widthPixels + " x " + metrics.heightPixels);
        Log.d("DeviceInfo", "scale = " + metrics.density);
    }

    @SuppressWarnings("unchecked")
    private static void buildReverseSoundMap()
    {
        sSoundMapReverse = new HashMap<String, String>();

        Iterator<Map.Entry<String, Object>> it = sSoundMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Object> pairs = it.next();

            HashMap<String, Object> map = (HashMap<String, Object>)pairs.getValue();
            String[] titles             = (String[])map.get("title");

            for (String title : titles)
            {
                sSoundMapReverse.put(title, pairs.getKey());
            }
        }
    }
}
