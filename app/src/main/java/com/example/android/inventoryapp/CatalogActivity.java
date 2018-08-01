package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity {
    InventoryDbHelper mDbHelper;
    SQLiteDatabase mInventoryDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Fb button with explicit intent to the edit page
        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(editIntent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new InventoryDbHelper(this);

        //displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * method that returns a cursor with all columns
     */
    private Cursor queryAllData() {
        //open your databse to read it.
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {BookEntry._ID, BookEntry.COLUMN__PRODUCT_NAME, BookEntry.COLUMN_PRICE, BookEntry.COLUMN_IN_STOCK, BookEntry.COLUMN_QUANTITY, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Perform a query on the books table
        return getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null,null);

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books database.
     */
    private void displayDatabaseInfo() {
        //calls the method that read the information on the DB
        cursor = queryAllData();

        //fetch the TextView where the information will be displayed
        TextView displayView = (TextView) findViewById(R.id.text_view_books);

        try {
            // Create a header in the Text View
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(BookEntry._ID + " - " + BookEntry.COLUMN__PRODUCT_NAME + " - " + BookEntry.COLUMN_PRICE + " - " + BookEntry.COLUMN_IN_STOCK + " - " + BookEntry.COLUMN_QUANTITY + " - " + BookEntry.COLUMN_SUPPLIER_NAME + " - " + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN__PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int stockColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_IN_STOCK);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int phoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentStock = cursor.getInt(stockColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentPhoneNumber = cursor.getString(phoneNumberColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " + currentProductName + " - " + currentPrice + " - " + currentStock + " - " + currentQuantity + " - " + currentSupplierName + " - " + currentPhoneNumber));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * INSERT DUMMY DATA
     */
    private void insertBooks() {

        //you create an object  of ContentValues.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN__PRODUCT_NAME, "English File");
        values.put(BookEntry.COLUMN_PRICE, 22.22);
        values.put(BookEntry.COLUMN_IN_STOCK, 1);
        values.put(BookEntry.COLUMN_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Midleton");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "555-345-234");

        //insert a new roa in the table with a specific ID
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * MENU methods
     */
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
