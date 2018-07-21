package com.example.android.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        TextView bookView = (TextView) findViewById(R.id.text_view_books);
        bookView.append("YOUPI");
    }
}
