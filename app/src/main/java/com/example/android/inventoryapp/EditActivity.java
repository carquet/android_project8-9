package com.example.android.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;


/** allows user to create or update a new product from the inventory */
public class EditActivity extends AppCompatActivity {

    private EditText productName;
    private EditText supplierName;
    private EditText supplierPhoneNumber;
    private Spinner inStockSpinner;
    private EditText price;
    private EditText quantity;

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
        productName = (EditText) findViewById(R.id.edit_product_name);
        supplierName = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);
        inStockSpinner = (Spinner) findViewById(R.id.spinner_in_stock);
        price = (EditText) findViewById(R.id.edit_price);
        quantity = (EditText) findViewById(R.id.edit_quantity);


        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter stockSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.in_stock_array, android.R.layout.simple_spinner_item);

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


}
