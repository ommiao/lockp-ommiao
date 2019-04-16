package com.hua.lockp;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class HomeService extends AccessibilityService {

    private static final String PACKAGE_SYSTEMUI = "com.android.systemui";

    private String homeString;

    @Override
    public void onCreate() {
        super.onCreate();
        homeString = getVitualNavigationKey(this, "accessibility_home", PACKAGE_SYSTEMUI);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Intent notify = new Intent(MainActivity.HOME_ASSESSIBILITY_OPEN);
        sendBroadcast(notify);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(PACKAGE_SYSTEMUI.contentEquals(accessibilityEvent.getPackageName()) &&
                homeString.contentEquals(accessibilityEvent.getContentDescription())){
            Log.d("ShouldLock", "ShouldLock! ");
            Intent it = new Intent(this, MyReceiver.class);
            it.putExtra("type",666);
            sendBroadcast(it);
        }
    }

    @Override
    public void onInterrupt() {
        Intent notify = new Intent(MainActivity.HOME_ASSESSIBILITY_CLOSE);
        sendBroadcast(notify);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent notify = new Intent(MainActivity.HOME_ASSESSIBILITY_CLOSE);
        sendBroadcast(notify);
    }

    public String getVitualNavigationKey(Context context, String name, String packageName)
    {
        try
        {
            Resources packageManager = context.getPackageManager().getResourcesForApplication(packageName);
            return packageManager.getString(packageManager.getIdentifier(name, "string", packageName));
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
