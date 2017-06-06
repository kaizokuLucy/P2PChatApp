package com.example.lucija.p2pchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ReceiveActivity extends AppCompatActivity {

    PrintWriter out;
    BufferedReader in;

    private ListView messagingListView;
    private View sendButton;
    private EditText messageText;
    private List<ChatMessage> messagesList;
    private ArrayAdapter<ChatMessage> adapter;

    private Handler handler = new Handler();

    String name;
    String number;
    int id;
    MyDBHandler myDBHandler = new MyDBHandler(this, null, null, 2);
    CryptoUtil cryptoUtil;
    Contact contact;

    boolean connected = false;
    String serverIpAddress;
    private Socket socket;

    IntentFilter intentFilter;


    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //ChatMessage receievedMessage = new ChatMessage(intent.getStringExtra("message"), false);
            String[] parts = intent.getStringExtra("message").split("LUCIJA");
            Log.d("Sms", intent.getStringExtra("message"));

            try {
                serverIpAddress = parts[1];
                number = parts[0];
                number = number.replaceFirst("\\+385", "0");
                contact = myDBHandler.getContactByNumber(number);
                cryptoUtil = new CryptoUtil(contact.getMyKey(), contact.getContactKey());
                name = contact.get_contactName();
                setTitle(name);
                id = contact.get_id();
            }
            catch(Exception e){
                ChatMessage chatMessage = new ChatMessage(e.getMessage() + "\n" + number + "\n" + serverIpAddress, false);
                messagesList.add(chatMessage);
                adapter.notifyDataSetChanged();
                Log.d("PORUKA", "luffy");
            }
            /*ChatMessage chatMessage = new ChatMessage(name + "\n" + number + "\n" + serverIpAddress, false);
            messagesList.add(chatMessage);
            adapter.notifyDataSetChanged();*/
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
                    String encrypted;
                    try{
                        encrypted = cryptoUtil.encrypt(messageText.getText().toString());
                        out.println(encrypted);
                    } catch (Exception e) {
                        encrypted = "Encryption failed!";
                        Toast.makeText(ReceiveActivity.this, encrypted, Toast.LENGTH_LONG).show();
                    }
                    messageText.setText("");
                }
            }
        });
    }

    private void connect(){
        if (!connected) {
            if (!serverIpAddress.equals("")) {
                Thread cThread = new Thread(new ClientThread());
                Log.d("asdf", "nami");
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
              //  InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                socket = new Socket(serverIpAddress, ConversationActivity.SERVERPORT);
                connected = true;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                       .getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");

                        // WHERE YOU ISSUE THE COMMANDS
                        //out.println("Hey " + name + " !");
                        //out.println("Hello " + name + " !");
                        Log.d("ClientActivity", "C: Sent.");

                        String line = in.readLine();
                        class MyRunnable implements Runnable {
                            String str;

                            public MyRunnable(String str) {
                                this.str = str;
                            }

                            @Override
                            public void run() {
                                receiveMessage(str);
                            }
                        }
                        if(!line.isEmpty()) {
                            handler.post(new MyRunnable(line));
                        }

                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                //socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
    public void receiveMessage(String message) {
        String decrypted;
        try{
            decrypted = cryptoUtil.decrypt(message);
        }
        catch (Exception e) {
            decrypted = "Failed message decrypting!";
        }
        ChatMessage receivedMessage = new ChatMessage(decrypted, false);
        messagesList.add(receivedMessage);
        adapter.notifyDataSetChanged();
    }
}
