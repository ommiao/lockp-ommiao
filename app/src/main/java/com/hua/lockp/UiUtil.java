package com.hua.lockp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UiUtil {

    public static final int TOAST_EMOJI_POSITIVE = 0;

    public static final int TOAST_EMOJI_NEGATIVE = 1;

    public static final int TOAST_EMOJI_NEUTRAL = 2;

    private static Context mContext = Myapplication.getContext();

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

}
