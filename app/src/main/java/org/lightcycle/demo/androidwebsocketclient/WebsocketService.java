package org.lightcycle.demo.androidwebsocketclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

public class WebsocketService extends Service {
    public static final String BROADCAST_ACTION = WebsocketService.class.getName() + ".BROADCAST";

    private WebSocket webSocket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMessage("Starting service.");
        String address = intent.getStringExtra(getString(R.string.extra_address));
        if (address.trim().isEmpty()) {
            sendMessage("No address specified, not connecting.");
        } else {
            connect(address);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        sendMessage("Stopped service.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(String message) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("msg", message);
        sendBroadcast(intent);
    }

    private void connect(String address) {
        sendMessage("Connecting to " + address);
        AsyncHttpClient.getDefaultInstance().websocket(address, "example", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception e, WebSocket webSocket) {
                if (e != null) {
                    sendMessage("Failed to connect: " + e.getMessage());
                    return;
                }
                sendMessage("Connected.");
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String message) {
                        sendMessage("Received string \"" + message + "\"");
                    }
                });
                WebsocketService.this.webSocket = webSocket;
            }
        });
    }

    private void disconnect() {
        if (webSocket == null) {
            webSocket.end();
        }
    }
}
