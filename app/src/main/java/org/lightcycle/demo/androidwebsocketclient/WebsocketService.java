package org.lightcycle.demo.androidwebsocketclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.nio.ByteBuffer;

public class WebsocketService extends Service {
    public static final String BROADCAST_ACTION = WebsocketService.class.getName() + ".BROADCAST";

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        WebsocketService getService() {
            return WebsocketService.this;
        }
    }

    private WebSocket webSocket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMessage("Web socket service started.");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sendMessage("Web socket service destroyed.");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void sendMessage(String message) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("msg", message);
        sendBroadcast(intent);
    }

    public void connect(String address) {
        sendMessage("Connecting to \"" + address + "\"");
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
                webSocket.setDataCallback(new DataCallback() {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                        ByteBuffer bytes = bb.getAll();
                        StringBuffer buffer = new StringBuffer();
                        while (bytes.hasRemaining()) {
                            buffer.append(String.format("%02X", bytes.get()));
                        }
                        sendMessage("Received binary 0x" + buffer.toString());
                        bb.recycle();
                    }
                });
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception e) {
                        if (e != null) {
                            sendMessage("Disconnected with exception: " + e.getMessage());
                        } else {
                            sendMessage("Disconnected.");
                        }
                    }
                });
                WebsocketService.this.webSocket = webSocket;
            }
        });
    }

    public boolean isConnected() {
        return webSocket != null && webSocket.isOpen();
    }

    public void disconnect() {
        if (isConnected()) {
            webSocket.close();
        }
    }

    public void sendText(String text) {
        if (isConnected()) {
            sendMessage("Sending string \"" + text + "\"");
            webSocket.send(text);
        }
    }

    public void sendBinary(byte[] bytes) {
        if (isConnected()) {
            StringBuffer buffer = new StringBuffer();
            for (byte b : bytes) {
                buffer.append(String.format("%02X", b));
            }
            sendMessage("Sending binary 0x" + buffer.toString());
            webSocket.send(bytes);
        }
    }
}
