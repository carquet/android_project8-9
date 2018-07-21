package com.example.android.inventoryapp;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.BookContract;

public class CatalogActivity extends AppCompatActivity {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase mInventoryDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /* instance of SQLiteOpenHelper to access our database, we pass the context of the activity */
        mDbHelper = new InventoryDbHelper(this);

        // Create and/or open a database to read from it
        //mInventoryDb = mDbHelper.getReadableDatabase();

        TextView bookView = (TextView) findViewById(R.id.text_view_books);
        bookView.append("YOUPI");

    }
}
