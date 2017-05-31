package com.example.lucija.p2pchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String string = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus != null ? pdus.length : 0];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) (pdus != null ? pdus[i] : null));
                string += messages[i].getOriginatingAddress();
                string += "LUCIJA";
                string += messages[i].getMessageBody();
            }
            //display message
            //Toast.makeText(context, string, Toast.LENGTH_SHORT).show();

            //send a broadcast intent to update the sms receiver in a textView
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("message", string);
            context.sendBroadcast(broadcastIntent);
        }
    }
}
