package com.zman2245.pinpin.view.listitem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zman2245.pinpin.R;

/**
 * A quiz choice list item
 * 
 * @author Zack
 */
public class ViewQuizChoice extends RelativeLayout
{
    private ImageView mImage;
    private TextView  mText;

    public ViewQuizChoice(Context context)
    {
        super(context);
    }

    public ViewQuizChoice(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ViewQuizChoice(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        mImage = (ImageView) findViewById(R.id.image);
        mText = (TextView) findViewById(R.id.choice);
    }

    public void setText(String text)
    {
        mText.setText(text);
    }

    public void setCorrect()
    {
        mImage.setImageResource(R.drawable.check_green);
        mImage.setVisibility(View.VISIBLE);
    }

    public void setIncorrect()
    {
        mImage.setImageResource(R.drawable.x_red);
        mImage.setVisibility(View.VISIBLE);
    }
}
