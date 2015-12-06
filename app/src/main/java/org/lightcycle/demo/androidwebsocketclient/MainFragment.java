package org.lightcycle.demo.androidwebsocketclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class MainFragment extends RoboFragment {
    @InjectView(R.id.log_messages_scrollview)
    private ScrollView logMessagesScrollView;

    @InjectView(R.id.log_messages)
    private TextView logMessages;

    private BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getActivity(), intent.getStringExtra("msg"), Toast.LENGTH_SHORT);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clearlog:
                clearMessages();
                return true;
            case R.id.action_startservice:
                startWebsocket();
                return true;
            case R.id.action_stopservice:
                stopWebsocket();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void addMessage(String message) {
        logMessages.append(message);
        logMessages.append("\n");
        logMessagesScrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void clearMessages() {
        logMessages.setText("");
    }

    private void startWebsocket() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Intent intent = new Intent(getActivity().getBaseContext(), WebsocketService.class);
        intent.putExtra(getString(R.string.extra_address), prefs.getString(getString(R.string.pref_server_url_key), getString(R.string.pref_server_url_default)));
        getActivity().startService(intent);
    }

    private void stopWebsocket() {
        getActivity().stopService(new Intent(getActivity().getBaseContext(), WebsocketService.class));
    }
}
