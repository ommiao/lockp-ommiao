package com.hua.lockp;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TileLockSevice extends TileService {

    @Override
    public void onClick() {
        Intent it = new Intent(this, MyReceiver.class);
        it.putExtra("type",666);
        sendBroadcast(it);
    }
}
