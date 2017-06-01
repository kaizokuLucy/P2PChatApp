package com.example.lucija.p2pchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView lucysListView;
    ArrayList<Contact> contactList;
    MyDBHandler myDBHandler = new MyDBHandler(this, null, null, 2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lucysListView = (ListView) findViewById(R.id.contactsListView);
        //Contact prob = new Contact("Danijel", "0955964666", "Kdasf232", "Ffs323o");
        //myDBHandler.addContact(prob);
        printDatabase();
    }

    @Override
    public void onResume(){
        super.onResume();
        printDatabase();
    }

    public void addButtonClicked(View view) {
       // Toast.makeText(HomeActivity.this, "treba se dodat", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(HomeActivity.this, AddContactActivity.class);
        startActivity(intent);

        printDatabase();
    }

    public void listenButtonClicked(View view){
        Intent intent = new Intent(HomeActivity.this, ReceiveActivity.class);
        startActivity(intent);
    }

    public void printDatabase() {

        contactList = myDBHandler.getContacts();
        String[] contactsName;
        int i = 0;

        if (contactList == null) {
            Toast.makeText(HomeActivity.this, "nemate niti jednog kontakta", Toast.LENGTH_SHORT).show();
            //String[] onePiece = {"Luffy", "Zoro", "Usopp", "Sanji", "Nami", "Chopper", "Robin", "Franky", "Brooke"};
            //ListAdapter lucysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsName);
            //lucysListView.setAdapter(lucysAdapter);
        } else {
            contactsName = new String[contactList.size()];
            for (Contact c : contactList) {
                try {
                    contactsName[i] = c.get_contactName();
                    i++;
                } catch (Exception e) {}

            }
            ListAdapter lucysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsName);
            lucysListView.setAdapter(lucysAdapter);
        }

        lucysListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                        Contact contactDetail = contactList.get(position);
                        intent.putExtra("name", contactDetail.get_contactName());
                        intent.putExtra("number", contactDetail.getContactNumber());
                        intent.putExtra("id", contactDetail.get_id());
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }
}
