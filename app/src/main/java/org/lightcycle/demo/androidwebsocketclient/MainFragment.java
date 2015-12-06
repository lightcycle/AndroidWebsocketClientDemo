package org.lightcycle.demo.androidwebsocketclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class MainFragment extends RoboFragment {
    @InjectView(R.id.log_messages_scrollview)
    private ScrollView logMessagesScrollView;

    @InjectView(R.id.log_messages)
    private TextView logMessages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMessage(String message) {
        logMessages.append(message);
        logMessages.append("\n");
        logMessagesScrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void clearMessages() {
        logMessages.setText("");
    }
}
