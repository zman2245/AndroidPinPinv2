package com.zman2245.pinpin.fragment.learn;

import java.io.IOException;
import java.util.HashMap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zman2245.pinpin.AppPinPin;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.data.DataItemPractice;
import com.zman2245.pinpin.log.EventLog;
import com.zman2245.pinpin.util.audio.UtilAudioPlayer;

/**
 * A fragment for a practice word
 *
 * @author zack
 */
public class FragmentPractice extends Fragment implements OnClickListener
{
    private static final String KEY_DATA = "data";

    private static String mFileName = null;
    static
    {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer   = null;

    private String[] mTones;
    private String mParentWord;
    private String mCurrentWord;

    private TextView mTitle;

    private ImageButton mFirst;
    private ImageButton mSecond;
    private ImageButton mThird;
    private ImageButton mFourth;

    private ImageButton mMicrophone;
    private ImageButton mRecord;
    private ImageButton mPlayback;

    private View mContainer;

    /**
     * FragmentPractice construction
     *
     * @param data  The data this fragment will present
     * @return A new instance of FragmentPractice
     */
    public static FragmentPractice newInstance(DataItemPractice data)
    {
        FragmentPractice frag  = new FragmentPractice();
        Bundle args            = new Bundle();

        args.putSerializable(KEY_DATA, data);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_practice, container, false);

        mTitle = (TextView)rootView.findViewById(R.id.title);

        mFirst  = (ImageButton)rootView.findViewById(R.id.btn_tone_first);
        mSecond = (ImageButton)rootView.findViewById(R.id.btn_tone_second);
        mThird  = (ImageButton)rootView.findViewById(R.id.btn_tone_third);
        mFourth = (ImageButton)rootView.findViewById(R.id.btn_tone_fourth);
        mMicrophone = (ImageButton)rootView.findViewById(R.id.btn_microphone);
        mRecord     = (ImageButton)rootView.findViewById(R.id.btn_record);
        mPlayback   = (ImageButton)rootView.findViewById(R.id.btn_play);

        mFirst.setOnClickListener(this);
        mSecond.setOnClickListener(this);
        mThird.setOnClickListener(this);
        mFourth.setOnClickListener(this);
        mMicrophone.setOnClickListener(this);
        mRecord.setOnClickListener(this);
        mPlayback.setOnClickListener(this);

        mPlayback.setEnabled(false);

        fixCorruption();

        DataItemPractice data = (DataItemPractice)getArguments().getSerializable(KEY_DATA);
        mParentWord = data.word;
        HashMap<String, Object> map = (HashMap<String, Object>)AppPinPin.sSoundMap.get(mParentWord);
        mTones = (String[])map.get("title");

        mCurrentWord = mTones[0];
        mTitle.setText(mTones[0]);
        mFirst.setSelected(true);

        playSoundForCurrentWord();

        mContainer = rootView.findViewById(R.id.outer_container);
        mContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateOut();
            }
        });

        mPlayback.setEnabled(false);

        return rootView;
    }

    // TODO: should find real bug and take this out
    @SuppressWarnings("unchecked")
    private void fixCorruption()
    {
        // uber hack, sometimes the sound map gets corrupted :( I don't know why yet
        HashMap<String, Object> map = (HashMap<String, Object>)AppPinPin.sSoundMap.get("fang");
        mTones = (String[])map.get("title");
        if (!mTones[0].equals("fāng") || !mTones[1].equals("fáng") || !mTones[2].equals("fǎng") || !mTones[3].equals("fàng"))
        {
            Log.d("DEBUG", "rebuilding sound map");
            AppPinPin.rebuildSoundMap();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mRecorder != null)
        {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null)
        {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_tone_first:
            EventLog.trackEvent(R.string.flurry_event_sound_practice_tone);
            mCurrentWord = mTones[0];
            mTitle.setText(mTones[0]);
            playSoundForCurrentWord();
            clearSelectedTone();
            mFirst.setSelected(true);
            break;

        case R.id.btn_tone_second:
            EventLog.trackEvent(R.string.flurry_event_sound_practice_tone);
            mCurrentWord = mTones[1];
            mTitle.setText(mTones[1]);
            playSoundForCurrentWord();
            clearSelectedTone();
            mSecond.setSelected(true);
            break;

        case R.id.btn_tone_third:
            EventLog.trackEvent(R.string.flurry_event_sound_practice_tone);
            mCurrentWord = mTones[2];
            mTitle.setText(mTones[2]);
            playSoundForCurrentWord();
            clearSelectedTone();
            mThird.setSelected(true);
            break;

        case R.id.btn_tone_fourth:
            EventLog.trackEvent(R.string.flurry_event_sound_practice_tone);
            mCurrentWord = mTones[3];
            mTitle.setText(mTones[3]);
            playSoundForCurrentWord();
            clearSelectedTone();
            mFourth.setSelected(true);
            break;

        case R.id.btn_microphone:
            playSoundForCurrentWord();
            EventLog.trackEvent(R.string.flurry_event_sound_practice_listen);
            break;

        case R.id.btn_record:
            mPlayback.setEnabled(true);
            if (mRecorder == null)
            {
                EventLog.trackEvent(R.string.flurry_event_sound_practice_record);
                startRecording();
            }
            else
            {
                stopRecording();
            }
            break;

        case R.id.btn_play:
            if (mPlayer != null && mPlayer.isPlaying())
            {
                mPlayback.setImageResource(R.drawable.play);
                mPlayer.pause();
            }
            else if (mPlayer != null)
            {
                mPlayback.setImageResource(R.drawable.pause);
                mPlayer.start();
            }
            else
            {
                EventLog.trackEvent(R.string.flurry_event_sound_practice_playback);
                mPlayback.setImageResource(R.drawable.pause);
                startPlaying();
            }
            break;

        default:
            break;
        }
    }

    // public api

    public void animateOut()
    {
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_default);
        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }
            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                getActivity().finish();
            }
        });
        mContainer.startAnimation(fadeOut);
    }

    // private helpers

    private void startRecording()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            String[] permissionsNeeded = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissionsNeeded, 3001);
        } else {

            if (mPlayer != null)
                mPlayer.release();

            mPlayer = null; // reset player
            mPlayback.setEnabled(false);
            mRecord.setImageResource(R.drawable.recorder_orange);
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setMaxDuration(30000);

            mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        stopRecording();
                    }
                }
            });

            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.toast_practice_file_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void stopRecording()
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mRecord.setImageResource(R.drawable.recorder_light);
        mPlayback.setEnabled(true);
    }

    private void startPlaying()
    {
        mPlayer = new MediaPlayer();
        try
        {
            mPlayer.setDataSource(mFileName);

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mPlayback.setImageResource(R.drawable.play);
                }
            });

            mPlayer.prepare();
            mPlayer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void playSoundForCurrentWord()
    {
        int resId = AppPinPin.getAudioMapper().getResourceForString(mCurrentWord);
        UtilAudioPlayer.playSound(resId);
    }

    private void clearSelectedTone()
    {
        mFirst.setSelected(false);
        mSecond.setSelected(false);
        mThird.setSelected(false);
        mFourth.setSelected(false);
    }
}
