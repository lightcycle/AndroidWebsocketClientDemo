package org.lightcycle.demo.androidwebsocketclient;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class MainFragment extends RoboFragment implements ServiceConnection {
    @InjectView(R.id.log_messages_scrollview)
    private ScrollView logMessagesScrollView;

    @InjectView(R.id.log_messages)
    private TextView logMessages;

    private BroadcastReceiver receiver;

    private WebsocketService wsService;

    private boolean serviceBound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                addMessage(intent.getStringExtra("msg"));
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(WebsocketService.BROADCAST_ACTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_connect).setEnabled(serviceBound && !wsService.isConnected());
        menu.findItem(R.id.action_sendtext).setEnabled(serviceBound && wsService.isConnected());
        menu.findItem(R.id.action_disconnect).setEnabled(serviceBound && wsService.isConnected());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = new Intent(getActivity(), WebsocketService.class);
        getActivity().getApplicationContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clearlog:
                clearMessages();
                return true;
            case R.id.action_connect:
                if (serviceBound) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    wsService.connect(prefs.getString(getString(R.string.pref_server_url_key), getString(R.string.pref_server_url_default)));
                }
                return true;
            case R.id.action_sendtext:
                if (serviceBound) {
                    wsService.sendText(RandomStringUtils.randomAlphabetic(10));
                }
                return true;
            case R.id.action_disconnect:
                if (serviceBound) {
                    wsService.disconnect();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        if (getActivity().isFinishing()) {
            if (serviceBound) {
                getActivity().getApplicationContext().unbindService(this);
                serviceBound = false;
            }
        }
    }

    private void addMessage(String message) {
        logMessages.append(message);
        logMessages.append("\n");
        logMessagesScrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void clearMessages() {
        logMessages.setText("");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        WebsocketService.LocalBinder binder = (WebsocketService.LocalBinder)service;
        wsService = binder.getService();
        serviceBound = true;
        addMessage("Activity connected to web socket service.");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceBound = false;
        addMessage("Activity disconnected from web socket service.");
    }
}
