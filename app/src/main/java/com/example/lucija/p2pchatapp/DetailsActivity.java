package com.example.lucija.p2pchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class DetailsActivity extends AppCompatActivity {

    String name;
    String number;

    TextView nameText;
    TextView numberText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        setTitle("Details");

        nameText = (TextView) findViewById(R.id.TextViewName);
        numberText = (TextView) findViewById(R.id.numberText);

        Bundle contactData = getIntent().getExtras();

        name = contactData.getString("name");
        number = contactData.getString("number");

        nameText.setText(name);
        numberText.setText(number);

        Toast.makeText(this, String.valueOf(getIntent().getIntExtra("id", -1)), Toast.LENGTH_LONG).show();
    }

    public void onDeleteClicked(View view){
        MyDBHandler myDBHandler = new MyDBHandler(this, null, null, 2);
        myDBHandler.deleteContact(getIntent().getIntExtra("id", -1));
        Intent intent = new Intent(DetailsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
