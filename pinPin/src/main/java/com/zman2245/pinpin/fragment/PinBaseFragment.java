package com.zman2245.pinpin.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.zman2245.pinpin.fragment.event.Event;
import com.zman2245.pinpin.fragment.event.FragmentEventListener;

/**
 * Base fragment class
 * 
 * @author zfoster
 */
public class PinBaseFragment extends Fragment implements FragmentEventListener
{
    /**
     * Called by parent activity to give fragment a chance to handle the back
     * button press
     * 
     * @return true if the back button even was handled
     */
    public boolean onBackPressed()
    {
        return false;
    }

    /**
     * Send an event to this fragment's parent (could be a parent fragment or an
     * activity)
     * 
     * @param event
     *            The event to send
     */
    protected void sendEvent(Event event)
    {
        Fragment frag = getParentFragment();
        if (frag != null && frag instanceof FragmentEventListener)
        {
            ((FragmentEventListener) frag).handleEvent(event);
            return;
        }

        Activity activity = getActivity();
        if (activity != null && activity instanceof FragmentEventListener)
        {
            ((FragmentEventListener) activity).handleEvent(event);
        }
    }

    @Override
    public void handleEvent(Event event)
    {
        sendEvent(event);
    }

    protected void enableHomeAsUp(boolean enable)
    {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(enable);
        getActivity().getActionBar().setHomeButtonEnabled(enable);
    }
}
