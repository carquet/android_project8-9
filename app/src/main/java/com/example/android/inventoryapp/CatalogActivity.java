package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    private void insertBooks(){

        //put the database into writable mode
        mInventoryDb = mDbHelper.getWritableDatabase();

        //you create an object  of ContentValues.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN__PRODUCT_NAME, "English File");
        values.put(BookEntry.COLUMN_PRICE, 22);
        values.put(BookEntry.COLUMN_IN_STOCK, 1);
        values.put(BookEntry.COLUMN_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Midleton");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "555-345-234");

        //insert a new roa in the table with a specific ID
        long newRowId = mInventoryDb.insert(BookEntry.TABLE_NAME, null, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBooks();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
