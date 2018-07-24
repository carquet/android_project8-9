package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import org.w3c.dom.Text;


/**
 * allows user to create or update a new product from the inventory
 */
public class EditActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNumberEdittext;
    private Spinner inStockSpinner;
    private EditText priceEditText;
    private EditText quantityEdittext;


    /**
     * Gender of the pet. The possible valid values are in the BookContract.java file:
     * {@link BookEntry#IN_STOCK}, {@link BookEntry#NOT_IN_STOCK}
     */
    private int stock = BookEntry.NOT_IN_STOCK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Find all relevant views that we will need to read user input from
        productNameEditText = (EditText) findViewById(R.id.edit_product_name);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneNumberEdittext = (EditText) findViewById(R.id.edit_supplier_phone_number);
        inStockSpinner = (Spinner) findViewById(R.id.spinner_in_stock);
        priceEditText = (EditText) findViewById(R.id.edit_price);
        quantityEdittext = (EditText) findViewById(R.id.edit_quantity);

        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter stockSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.in_stock_array, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        stockSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        inStockSpinner.setAdapter(stockSpinnerAdapter);

        // Set the integer mSelected to the constant values
        inStockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.in_stock))) {
                        stock = BookEntry.IN_STOCK;
                    } else {
                        stock = BookEntry.NOT_IN_STOCK;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                stock = BookEntry.NOT_IN_STOCK;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save books to inventory database
                insert();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insert() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String productNameString = productNameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        float price = Float.parseFloat(priceString);
        String quantityString = quantityEdittext.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = supplierPhoneNumberEdittext.getText().toString().trim();

        //create the DB helper
        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);
        //put the database into writable mode
        SQLiteDatabase mInventoryDb = mDbHelper.getWritableDatabase();

        //you create an object  of ContentValues.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN__PRODUCT_NAME, productNameString);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_IN_STOCK, stock);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        //insert a new roa in the table with a specific ID
        long newRowId = mInventoryDb.insert(BookEntry.TABLE_NAME, null, values);
    }
}
