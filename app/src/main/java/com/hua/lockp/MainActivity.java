package com.hua.lockp;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.system.ErrnoException;
import android.system.Os;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isServerRun = false;

    private ImageView icon;

    private Client client;

    MyHandler handler = new MyHandler(this);

    static class MyHandler extends Handler{

        private WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null && msg.what == 1){
                if(!activity.isServerRun){
                    activity.isServerRun = true;
                    activity.switchToSuccess();
                }
                removeMessages(2);
                sendEmptyMessageDelayed(2,2000);
            }
            if (activity != null && msg.what == 2 && activity.isServerRun){
                activity.isServerRun = false;
                activity.switchToFail();
            }
        }
    }

    private boolean run = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        icon = findViewById(R.id.iv_state);
        client =new Client(5467,new Client.MsgCallBack() {
            @Override
            public void onMsg(String text) {
                handler.sendEmptyMessage(1);
            }
        });
        new HeartThread().start();
        exJar();
        exSh();

        ImageView ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);

        CardView cardPcCmd = findViewById(R.id.cv_pc_cmd);
        CardView cardHyCmd = findViewById(R.id.cv_hy_cmd);
        CardView cardAddShortcut = findViewById(R.id.cv_add_shortcut);
        CardView cardSwitchFab = findViewById(R.id.cv_switch_fab);
        cardPcCmd.setOnClickListener(this);
        cardHyCmd.setOnClickListener(this);
        cardAddShortcut.setOnClickListener(this);
        cardSwitchFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_close:
                finish();
                break;
            case R.id.cv_pc_cmd:
                copyPcCmd();
                break;
            case R.id.cv_hy_cmd:
                copyHyCmd();
                break;
            case R.id.cv_add_shortcut:
                addShortCut();
                break;
            case R.id.cv_switch_fab:
                showHideFab();
                break;
        }
    }

    private void copyPcCmd(){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        ClipData myClip;
        myClip = ClipData.newPlainText("text", getString(R.string.pc_cmd));
        cm.setPrimaryClip(myClip);
        UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.pc_success));
    }

    private void copyHyCmd(){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        ClipData myClip;
        myClip = ClipData.newPlainText("text", getString(R.string.hy_cmd));
        cm.setPrimaryClip(myClip);
        UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.hy_success));
    }

    private void addShortCut(){
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            Intent shortcutInfoIntent = new Intent(this, ShortcutActivity.class);
            shortcutInfoIntent.setAction(Intent.ACTION_VIEW); //action必须设置，不然报错
            ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(this, "The only id")
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_launcher))
                    .setShortLabel("LockNow")
                    .setIntent(shortcutInfoIntent)
                    .build();

            //当添加快捷方式的确认弹框弹出来时，将被回调
            Intent it = new Intent();
            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(this, 123, it, 0);
            ShortcutManagerCompat.requestPinShortcut(this, info, shortcutCallbackIntent.getIntentSender());
        }
    }

    private void showHideFab(){
        UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.building));
    }

    private void switchToSuccess(){
        switchPicture(true);
    }

    private void switchToFail(){
        switchPicture(false);
    }

    private void switchPicture(final boolean success){
        Animation shrink = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(250);
        shrink.setInterpolator(new AccelerateDecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(success){
                    icon.setImageResource(R.drawable.icon_success);
                } else {
                    icon.setImageResource(R.drawable.icon_fail);
                }
                UiUtil.scaleAnimation(icon, 0f, 1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        icon.startAnimation(shrink);
    }

    public void exJar(){
        String fromPath = "server.jar";
        String toPath = getFilesDir().getParentFile() + "/" + "server.jar";
        FileUtil.copyAssetFile(this, fromPath, toPath);
        try {
            Os.chmod(getFilesDir().getParentFile().getAbsolutePath(),489);
            Os.chmod(toPath,420);
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
    }

    public void exSh(){
        String fromPath = "lockp.bash";
        String toPath =getFilesDir().getParentFile() + "/" + "lockp.bash";
        FileUtil.copyAssetFile(this, fromPath, toPath);
        try {
            Os.chmod(getFilesDir().getParentFile().getAbsolutePath(),489);
            Os.chmod(toPath,420);
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        run = false;
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    class HeartThread extends Thread{
        @Override
        public void run() {
            while (run){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.send(Client.HEART_BEAT);
            }
        }
    }

}
