package com.example.android.inventoryapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase mInventoryDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /* instance of SQLiteOpenHelper to access our database, we pass the context of the activity */
        //mDbHelper = new InventoryDbHelper(this);


        displayDatabaseInfo();

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new InventoryDbHelper(this);

        // Create and/or open a database to read from it
        mInventoryDb = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM books"
        // to get a Cursor that contains all rows from the books table.
        Cursor cursor = mInventoryDb.rawQuery("SELECT * FROM " + BookEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // books table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_books);
            displayView.setText("Number of rows in the books database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
