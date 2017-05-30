package com.example.lucija.p2pchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactActivity extends AppCompatActivity {

    EditText nameInputText;
    EditText numberInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        nameInputText = (EditText) findViewById(R.id.nameInputText);
        numberInputText = (EditText) findViewById(R.id.numberInputText);
    }

    public void okButtonClicked (View view){
        if(nameInputText.getText().toString().isEmpty() || numberInputText.getText().toString().isEmpty()){
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = new Intent(AddContactActivity.this, KeyActivity.class);
            intent.putExtra("name", nameInputText.getText().toString());
            intent.putExtra("number", numberInputText.getText().toString());
            startActivity(intent);
        }
    }
}
