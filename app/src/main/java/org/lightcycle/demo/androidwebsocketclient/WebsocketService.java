package org.lightcycle.demo.androidwebsocketclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WebsocketService extends Service {
    public static final String BROADCAST_ACTION = WebsocketService.class.getName() + ".BROADCAST";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
