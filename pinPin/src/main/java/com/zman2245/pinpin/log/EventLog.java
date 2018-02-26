package com.zman2245.pinpin.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.zman2245.pinpin.Registry;

/**
 * Contains static methods to track events
 *
 * This class wraps third party libraries interested in tracking events (e.g.
 * Flurry)
 *
 * @author zfoster
 */
public class EventLog
{
    private static final String                            LOG_TAG                   = EventLog.class.getSimpleName();

    private static final BlockingQueue<EventWorkerWrapper> sDispatchQueue            = new LinkedBlockingQueue<EventWorkerWrapper>();
    private static final EventLogWorker                    sDispatchWorker           = new EventLogWorker();

    private static OnSharedPreferenceChangeListener        sSharedPreferenceListener = new OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {

        }
    };

    /**
     * Static initializer that must be called from Application.onCreate()
     *
     * @param context
     */
    public static void init(Context context)
    {
        sDispatchWorker.start();
    }

    /**
     * Set a username that can be tied to tracked events
     *
     * @param name
     */
    public static void setUserName(String name)
    {
    }

    /**
     * Track a page view
     *
     * For Flurry, this increments a page view count
     *
     * @param page
     */
    public static void trackPageView()
    {
        EventWorkerWrapper wrapper = new EventWorkerWrapper();
        wrapper.type = EventLogType.EventLogTypePageView;

        sDispatchQueue.offer(wrapper);
    }

    /**
     * Track an event
     *
     * Just tracks an event by a simple event name
     *
     * @param eventName
     */
    public static void trackEvent(final String eventName)
    {
        EventWorkerWrapper wrapper = new EventWorkerWrapper();
        wrapper.type = EventLogType.EventLogTypeEvent;
        wrapper.eventName = eventName;

        sDispatchQueue.offer(wrapper);
    }

    /**
     * Track an event
     *
     * Just tracks an event by a simple event name
     *
     * @param stringResId
     */
    public static void trackEvent(int stringResId)
    {
        String eventName = Registry.sApp.getString(stringResId);

        EventWorkerWrapper wrapper = new EventWorkerWrapper();
        wrapper.type = EventLogType.EventLogTypeEvent;
        wrapper.eventName = eventName;

        sDispatchQueue.offer(wrapper);
    }

    public static void trackError(String errorId, String errorMessage, String errorClass)
    {
        EventWorkerWrapper wrapper = new EventWorkerWrapper();
        wrapper.type = EventLogType.EventLogTypeError;
        wrapper.eventName = errorId;
        wrapper.errorMessage = errorMessage;
        wrapper.errorClass = errorClass;

        sDispatchQueue.offer(wrapper);
    }

    public static OnSharedPreferenceChangeListener getPreferenceListener()
    {
        return sSharedPreferenceListener;
    }

    // Inner enum and static classes

    private enum EventLogType
    {
        EventLogTypePageView, EventLogTypeEvent, EventLogTypeError
    }

    private static class EventWorkerWrapper
    {
        EventLogType type;
        String       eventName;

        String       errorMessage;
        String       errorClass;
    }

    private static class EventLogWorker extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final EventWorkerWrapper wrapper = sDispatchQueue.take();

                    Log.d(LOG_TAG, String.format("Event handled: (type: %s. eventName: %s, errorMessage: %s, errorClass: %s", String.valueOf(wrapper.type),
                            String.valueOf(wrapper.eventName), String.valueOf(wrapper.errorMessage), String.valueOf(wrapper.errorClass)));

                    switch (wrapper.type)
                    {
                    case EventLogTypeEvent:
                        FlurryAgent.logEvent(wrapper.eventName);
                        break;

                    case EventLogTypePageView:
                        FlurryAgent.onPageView();
                        break;

                    case EventLogTypeError:
                        FlurryAgent.onError(wrapper.eventName, wrapper.errorMessage, wrapper.errorClass);
                        break;
                    }
                }
                catch (InterruptedException e)
                {
                    Log.d(LOG_TAG, "InterruptedException! " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
