package com.hua.lockp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShortcutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = new Intent(this, MyReceiver.class);
        it.putExtra("type",666);
        sendBroadcast(it);
        finish();
    }
}
