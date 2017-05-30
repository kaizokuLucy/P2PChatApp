package com.example.lucija.p2pchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

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
                    messagesList.add(chatMessage);
                    adapter.notifyDataSetChanged();
                    messageText.setText("");
                }
            }
        });

        //LOGIKA

        Bundle data = getIntent().getExtras();
        name = data.getString("name");
        number = data.getString("number");
        id = data.getInt("id");
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ConversationActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
