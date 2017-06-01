package com.example.lucija.p2pchatapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    // DEFAULT IP
    public static String SERVERIP = "Luffy";
    // DESIGNATE A PORT
    public static final int SERVERPORT = 4567;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;

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
        Bundle data = getIntent().getExtras();
        name = data.getString("name");
        number = data.getString("number");
        id = data.getInt("id");
        setTitle(name);

        SERVERIP = getLocalIpAddress();

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
                    //TODO send message to client
                }
            }
        });

        /*
        //DEBUG ENCRYPT AND DECRYPT METHODS FOM CRYPTO UTIL CLASS
        MyDBHandler myDBHandler = new MyDBHandler(this, null, null, 2);
        Contact contact = myDBHandler.getContactByNumber(number);
        CryptoUtil cryptoUtil = new CryptoUtil(contact.getMyKey(), contact.getMyKey());
        String debugText;
        try{
            debugText = cryptoUtil.encrypt("DEBUG TEXT");
        } catch(Exception e){
            debugText = "NIJE UPALILO";
        }
        ChatMessage chatMessage1 = new ChatMessage(debugText, true);
        messagesList.add(chatMessage1);
        adapter.notifyDataSetChanged();
        try{
            debugText = cryptoUtil.decrypt(debugText);
        } catch(Exception e){
            debugText = "NIJE UPALILO V2";
        }
        ChatMessage chatMessage2 = new ChatMessage(debugText, true);
        messagesList.add(chatMessage2);
        adapter.notifyDataSetChanged();
        //DEBUG
        */

        //LOGIC
        if(ContextCompat.checkSelfPermission(ConversationActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        //TODO build connction message
        String message = SERVERIP;

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("Message sent"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("Message delivered"), 0);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
            Toast.makeText(ConversationActivity.this, message, Toast.LENGTH_LONG).show();
            messageText.getText().clear();
        } catch(Exception e) {
            Toast.makeText(ConversationActivity.this, "Wasn't able to send the message", Toast.LENGTH_SHORT).show();
        }

        //START LISTENING
        Thread fst = new Thread(new ServerThread());
        fst.start();
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ConversationActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMesssage(String message){
        ChatMessage receievedMessage = new ChatMessage(message, false);
        messagesList.add(receievedMessage);
        adapter.notifyDataSetChanged();
    }

    public class ServerThread implements Runnable {

        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("mine", "Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("mine", "Connected.");
                            }
                        });

                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                Log.d("ServerActivity", line);
                                class MyRunnable implements Runnable{
                                    String str;
                                    public MyRunnable(String str){
                                        this.str = str;
                                    }
                                    @Override
                                    public void run() {
                                        receiveMesssage(str);
                                    }
                                }
                                handler.post(new MyRunnable(line));
                            }
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("mine", "Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("mine", "Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("mine", "Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }
}
