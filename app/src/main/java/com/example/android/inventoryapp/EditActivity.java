package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;



/**
 * allows user to create or update a new product from the inventory
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText productNameEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNumberEditText;
    private Spinner inStockSpinner;
    private EditText priceEditText;
    private EditText quantityEditText;


    /**
     * The possible valid values are in the BookContract.java file:
     * {@link BookEntry#IN_STOCK}, {@link BookEntry#NOT_IN_STOCK}
     */
    private int stock = BookEntry.NOT_IN_STOCK;

    //update information loader id
    int UPDATE_LOADER_ID = 2;

    //selected or updated row
    private Uri currentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //getting the intent from catalogue activity to update a product from the db
        Intent intent = getIntent();
        currentUri = intent.getData();
        //check if there is a uri in the intent
        if(currentUri == null){
            setTitle(getString(R.string.edit_activity_add_new_product));
        }else{
            setTitle(getString(R.string.edit_activity_update_existing_product));
            getLoaderManager().initLoader(UPDATE_LOADER_ID, null, this);
        }

        // Find all relevant views that we will need to read user input from
        productNameEditText = (EditText) findViewById(R.id.edit_product_name);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
        inStockSpinner = (Spinner) findViewById(R.id.spinner_in_stock);
        priceEditText = (EditText) findViewById(R.id.edit_price);
        quantityEditText = (EditText) findViewById(R.id.edit_quantity);

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
                save();
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

    private void save() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String productNameString = productNameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = supplierPhoneNumberEditText.getText().toString().trim();

            //defines an object to put the values
        ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN__PRODUCT_NAME, productNameString);
            float price = Float.parseFloat(priceString);
            values.put(BookEntry.COLUMN_PRICE, price);
            int quantity = Integer.parseInt(quantityString);
            values.put(BookEntry.COLUMN_QUANTITY, quantity);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);
            values.put(BookEntry.COLUMN_IN_STOCK, stock);


        //chekcs if it is a new product or an existing one by checking the existence of the uri
        if (currentUri == null) {
            //check whether all the information required is entered
            //insert a new row in the table with a specific ID
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();
            }

        }else{
            //insert a new row in the table with a specific ID
            int update = getContentResolver().update(currentUri, values, null, null);
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            if(update == 0){
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();

        }
    }






    /**
     * LOADER to communicate with the database and display information to be updated
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN__PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_IN_STOCK,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};


        //this loader will execute the content provider 's query method in the background.
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this,
                currentUri,             //the specific uri called
                projection,             //the column for the WHERE cl
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        if (cursorData.moveToFirst()){
            int productColumnIndex = cursorData.getColumnIndex(BookEntry.COLUMN__PRODUCT_NAME);
            int priceColumnIndex = cursorData.getColumnIndex(BookEntry.COLUMN_PRICE);
            int stockColumnIndex = cursorData.getColumnIndex(BookEntry.COLUMN_IN_STOCK);
            int quantityColumnIndex = cursorData.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursorData.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursorData.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String product = cursorData.getString(productColumnIndex);
            float priceFloat = cursorData.getFloat(priceColumnIndex);
            int stock = cursorData.getInt(stockColumnIndex);
            int quantityInt = cursorData.getInt(quantityColumnIndex);
            String supplier = cursorData.getString(supplierColumnIndex);
            String phoneNumber = cursorData.getString(phoneColumnIndex);

            //display information from the current product
            productNameEditText.setText(product);
            supplierNameEditText.setText(supplier);
            supplierPhoneNumberEditText.setText(phoneNumber);
            priceEditText.setText(String.valueOf(priceFloat));
            quantityEditText.setText(String.valueOf(quantityInt));
            switch (stock) {
                case BookEntry.IN_STOCK:
                    inStockSpinner.setSelection(0);
                    break;
                case BookEntry.NOT_IN_STOCK:
                    inStockSpinner.setSelection(1);
                    break;
                default:
                    inStockSpinner.setSelection(0);
                    break;

            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
