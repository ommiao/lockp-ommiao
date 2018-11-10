package com.hua.lockp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public class UiUtil {

    public static final int TOAST_EMOJI_POSITIVE = 0;

    public static final int TOAST_EMOJI_NEGATIVE = 1;

    public static final int TOAST_EMOJI_NEUTRAL = 2;

    private static Context mContext = Myapplication.getContext();

    private static SpringSystem mSpringSystem = SpringSystem.create();

    //弹出提示
    public static void shortToast(int emoji, String content){
        Toast toast = new Toast(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_toast_view, null);
        TextView textView = view.findViewById(R.id.toast_text);
        ImageView emojiView = view.findViewById(R.id.toast_emoji);
        textView.setText(content);
        switch (emoji){
            case TOAST_EMOJI_POSITIVE:
                emojiView.setImageResource(R.drawable.ic_toast_positive);
                break;
            case TOAST_EMOJI_NEUTRAL:
                break;
            case TOAST_EMOJI_NEGATIVE:
                emojiView.setImageResource(R.drawable.ic_toast_negative);
                break;
            default:
                break;
        }
        toast.setView(view);
        toast.show();
    }

    private static final double tension = 50;
    private static final double frictiion = 5;
    public static void scaleAnimation(final View target, float from, float to){
        Spring spring = mSpringSystem.createSpring();
        spring.setCurrentValue(from);
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, frictiion));
        spring.addListener(new SimpleSpringListener(){
            @Override
            public void onSpringUpdate(Spring spring) {
                target.setScaleX((float)spring.getCurrentValue());
                target.setScaleY((float)spring.getCurrentValue());
            }
        });
        spring.setEndValue(to);
    }

}
