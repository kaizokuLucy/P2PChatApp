package com.example.lucija.p2pchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyActivity extends AppCompatActivity {

    TextView keyText;
    EditText keyContact;
    SecretKey contactKey;
    String name, number, myKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        keyText = (TextView) findViewById(R.id.keyText);
        keyContact = (EditText) findViewById(R.id.keyContact);
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        myKey = "Default";
        try{
            myKey = Base64.encodeToString(CryptoUtil.getRawKey(getIntent().getStringExtra("number").getBytes()), Base64.DEFAULT);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        keyText.setText(myKey);
    }

    public void onCreateClicked (View view){
        if(keyContact.getText().toString().isEmpty()){
            Toast.makeText(this, "Must enter contact key!", Toast.LENGTH_LONG).show();
        }
        else{
            try{
                byte[] raw = (Base64.decode(keyContact.getText().toString(), Base64.DEFAULT));
                contactKey = new SecretKeySpec(raw, "AES");
                Contact newContact = new Contact(name, number, myKey, keyContact.getText().toString());
                MyDBHandler myDBHandler = new MyDBHandler(this, null, null, 2);
                myDBHandler.addContact(newContact);
                Intent intent = new Intent(KeyActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            catch(Exception e){
                Toast.makeText(this, "Not good, check contact key!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
