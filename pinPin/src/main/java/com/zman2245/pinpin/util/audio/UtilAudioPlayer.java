package com.zman2245.pinpin.util.audio;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.zman2245.pinpin.AppPinPin;

/**
 * Helpers for playing audio
 *
 * @author zack
 */
public class UtilAudioPlayer
{
    private static int DELAY_BETWEEN_SOUNDS_MS = 500;

    /**
     * Play the sound given by the resId, which should be a raw mp3 resource
     *
     * @param resId
     */
    public static void playSound(int resId)
    {
        playSound(resId, null);
    }

    public static void playSound(int resId, final OnCompletionListener listener)
    {
        MediaPlayer player = MediaPlayer.create(AppPinPin.getInstance().getApplicationContext(), resId);

        player.setLooping(false);
        player.setOnCompletionListener(new OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mp.release();

                if (listener != null)
                    listener.onCompletion(mp);
            }
        });

//        try
//        {
//            player.prepare();
//        }
//        catch (IllegalStateException e)
//        {
//            e.printStackTrace();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        player.start();
    }

    /**
     * Plays an array of sounds, one after the other, separated by a delay
     *
     * @param resIds
     */
    public static void playSounds(final int[] resIds)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int resId : resIds)
                {
                    try
                    {
                        synchronized (this)
                        {
                            wait(DELAY_BETWEEN_SOUNDS_MS);
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    playSound(resId);
                }
            }
        }).start();
    }
}
