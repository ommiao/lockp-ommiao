package com.hua.lockp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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

public class MainActivity extends AppCompatActivity {
    ImageView icon;
    Button pc;
    Button hy;
    TextView serverInfo;
    private Client client;
    MyHandler handler = new MyHandler(this);

    static class MyHandler extends Handler{

        private WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
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
        pc = findViewById(R.id.pc);
        hy = findViewById(R.id.hy);
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
        final ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将文本内容放到系统剪贴板里。
                assert cm != null;
                ClipData myClip;
                myClip = ClipData.newPlainText("text", getString(R.string.pc_cmd));
                cm.setPrimaryClip(myClip);
                UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.success));
            }
        });
        hy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将文本内容放到系统剪贴板里。
                assert cm != null;
                ClipData myClip;
                myClip = ClipData.newPlainText("text", getString(R.string.hy_cmd));
                cm.setPrimaryClip(myClip);
                UiUtil.shortToast(UiUtil.TOAST_EMOJI_NEUTRAL, getString(R.string.success));
            }
        });
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
