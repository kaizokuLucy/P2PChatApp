package com.example.lucija.p2pchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ReceiveActivity extends AppCompatActivity {

    private ListView messagingListView;
    private View sendButton;
    private EditText messageText;
    private List<ChatMessage> messagesList;
    private ArrayAdapter<ChatMessage> adapter;

    String name;
    String number;
    int id;
    MyDBHandler myDBHandler = new MyDBHandler(this, null, null, 2);

    boolean connected = false;
    String serverIpAddress;

    IntentFilter intentFilter;
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO connect using received message
            //ChatMessage receievedMessage = new ChatMessage(intent.getStringExtra("message"), false);
            String[] parts = intent.getStringExtra("message").split(":");
            serverIpAddress = parts[1];
            number = parts[0];
            Contact contact = myDBHandler.getContactByNumber(number);
            name = contact.get_contactName();
            id = contact.get_id();
            connect();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //filter for sms messages
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        registerReceiver(intentReceiver, intentFilter);

        messagesList = new ArrayList<>();

        //get references from main.xml
        messagingListView = (ListView) findViewById(R.id.messagingListView);
        sendButton = findViewById(R.id.sendButton);
        messageText = (EditText) findViewById(R.id.messageText);
        //set adapter for the listView
        adapter = new MessageAdapter(this, R.layout.left, messagesList);
        messagingListView.setAdapter(adapter);
        //event for button SEND
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageText.getText().toString().trim().equals("")) {
                    Toast.makeText(ReceiveActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    ChatMessage chatMessage = new ChatMessage(messageText.getText().toString(), true);
                    messagesList.add(chatMessage);
                    adapter.notifyDataSetChanged();
                    messageText.setText("");
                    //TODO send message to server
                }
            }
        });
    }

    private void connect(){
        if (!connected) {
            if (!serverIpAddress.equals("")) {
                Thread cThread = new Thread(new ClientThread());
                cThread.start();
            }
        }
        else{
            Toast.makeText(this, "You are already connected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(intentReceiver);
        super.onPause();
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, ConversationActivity.SERVERPORT);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        // WHERE YOU ISSUE THE COMMANDS
                        out.println("Hey Server!");
                        Log.d("ClientActivity", "C: Sent.");
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}
