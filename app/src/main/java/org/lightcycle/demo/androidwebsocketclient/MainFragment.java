package org.lightcycle.demo.androidwebsocketclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainFragment extends Fragment {
    private ScrollView logMessagesScrollView;
    private TextView logMessages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        logMessagesScrollView = (ScrollView)view.findViewById(R.id.log_messages_scrollview);
        logMessages = (TextView)logMessagesScrollView.findViewById(R.id.log_messages);

        return view;
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
