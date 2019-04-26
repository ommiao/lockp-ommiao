package com.hua.lockp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ShortcutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();
        Log.d("ShortcutActivity", "action: " + action);
        if(Intent.ACTION_CREATE_SHORTCUT.equals(action)){
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.lock));
            returnIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher_round));
            Intent it = new Intent(this, ShortcutActivity.class);
            returnIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, it);
            setResult(RESULT_OK, returnIntent);
        } else {
            Intent it = new Intent(this, MyReceiver.class);
            it.putExtra("type",666);
            sendBroadcast(it);
        }
        finish();
    }
}
