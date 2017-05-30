package com.example.lucija.p2pchatapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private ListView messagingListView;
    private View sendButton;
    private EditText messageText;
    private List<ChatMessage> messagesList;
    private ArrayAdapter<ChatMessage> adapter;

    String name;
    String number;
    int id;

    IntentFilter intentFilter;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //display message
            //TODO connect using received message
            ChatMessage receievedMessage = new ChatMessage(intent.getStringExtra("message"), false);
            messagesList.add(receievedMessage);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Bundle data = getIntent().getExtras();
        name = data.getString("name");
        number = data.getString("number");
        id = data.getInt("id");
        setTitle(name);

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
                    Toast.makeText(ConversationActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    ChatMessage chatMessage = new ChatMessage(messageText.getText().toString(), true);
                    Toast.makeText(ConversationActivity.this, chatMessage.isMine()?"Mine":"False", Toast.LENGTH_SHORT).show();
                    messagesList.add(chatMessage);
                    adapter.notifyDataSetChanged();
                    messageText.setText("");
                }
            }
        });

        //LOGIC
        if(ContextCompat.checkSelfPermission(ConversationActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        //filter for sms messages
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        registerReceiver(intentReceiver, intentFilter);

        //TODO build connction message
        String message = "DEBUG TEXT MESSAGE";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("Message sent"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("Message delivered"), 0);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
            Toast.makeText(ConversationActivity.this, "Sent connection SMS!", Toast.LENGTH_SHORT).show();
            messageText.getText().clear();
        } catch(Exception e) {
            Toast.makeText(ConversationActivity.this, "Wasn't able to send the message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ConversationActivity.this, HomeActivity.class);
        startActivity(intent);
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
}
