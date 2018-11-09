package com.hua.lockp;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView icon;
    TextView serverInfo;
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
                removeMessages(2);
                activity.icon.setImageResource(R.drawable.success);
                activity.serverInfo.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                activity.serverInfo.setText(R.string.ok_server);
                sendEmptyMessageDelayed(2,2000);
            }
            if (activity != null && msg.what == 2){
                activity.icon.setImageResource(R.drawable.fail);
            }
        }
    }

    private boolean run = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        icon = findViewById(R.id.iv_icon);
        serverInfo = findViewById(R.id.tv_server_info);
        client =new Client(5467,new Client.MsgCallBack() {
            @Override
            public void onMsg(String text) {
               handler.sendEmptyMessage(1);
            }
        });
        new HeartThread().start();
        exJar();
        exSh();

        Button pc = findViewById(R.id.pc);
        Button hy = findViewById(R.id.hy);
        Button addShortcut = findViewById(R.id.add_shortcut);
        Button showHideFab = findViewById(R.id.show_hide_fab);
        pc.setOnClickListener(this);
        hy.setOnClickListener(this);
        addShortcut.setOnClickListener(this);
        showHideFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pc:
                copyPcCmd();
                break;
            case R.id.hy:
                copyHyCmd();
                break;
            case R.id.add_shortcut:
                addShortCut();
                break;
            case R.id.show_hide_fab:
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
        UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.success));
    }

    private void copyHyCmd(){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        ClipData myClip;
        myClip = ClipData.newPlainText("text", getString(R.string.hy_cmd));
        cm.setPrimaryClip(myClip);
        UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.success));
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

    }

    public void exJar(){
        String fromPath = "server.jar";
        String toPath = getFilesDir().getParentFile() + "/" + "server.jar";
        Log.i("fuck", "exJar: "+toPath);
        Util.copyAssetFile(this, fromPath, toPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.chmod(getFilesDir().getParentFile().getAbsolutePath(),489);
                Os.chmod(toPath,420);
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }

    }

    public void exSh(){
        String fromPath = "lockp.bash";
        String toPath =getFilesDir().getParentFile() + "/" + "lockp.bash";
        Util.copyAssetFile(this, fromPath, toPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.chmod(getFilesDir().getParentFile().getAbsolutePath(),489);
                Os.chmod(toPath,420);
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        run = false;
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
