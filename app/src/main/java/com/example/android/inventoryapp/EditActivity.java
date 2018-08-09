package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.android.inventoryapp.data.BookContract.BookEntry;


/**
 * allows user to create or update a new product from the inventory
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //update information loader id
    private static final int UPDATE_LOADER_ID = 2;

    //selected or updated row
    private Uri currentUri;

    //field to edit information
    private EditText productNameEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNumberEditText;
    private EditText priceEditText;
    private EditText quantityEditText;

    private Spinner inStockSpinner;
    private Button increaseButton;
    private Button decreaseButton;
    private Button deleteProductButton;
    private Button orderProductButton;


    /**
     * The possible valid values are in the BookContract.java file:
     * {@link BookEntry#IN_STOCK}, {@link BookEntry#NOT_IN_STOCK}
     */
    private int stock = BookEntry.IN_STOCK;

    //NOTIFY: set up boolean to listen to any changed made and warn users when they leave the edit page
    private boolean bookHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            bookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        /*Opening Edit page in ADD PRODUCT OR UPDATE PRODUCT MODE*/
        //getting the intent from catalogue activity to update a product from the db
        Intent intent = getIntent();
        currentUri = intent.getData();

        //check if there is an existing  uri in the intent
        if (currentUri == null) {
            //the add product shows
            setTitle(getString(R.string.edit_activity_add_new_product));
            // the delete option is not shown
            //invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_activity_update_existing_product));
            getLoaderManager().initLoader(UPDATE_LOADER_ID, null, this);
        }

        // SET UP FRONT: Find all relevant views that we will need to read user input from
        productNameEditText = (EditText) findViewById(R.id.edit_product_name);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
        inStockSpinner = (Spinner) findViewById(R.id.spinner_in_stock);
        priceEditText = (EditText) findViewById(R.id.edit_price);
        quantityEditText = (EditText) findViewById(R.id.edit_quantity);
        increaseButton = (Button) findViewById(R.id.quantity_increase);
        decreaseButton = (Button) findViewById(R.id.quantity_decrease);
        deleteProductButton = (Button) findViewById(R.id.action_delete);
        orderProductButton = (Button) findViewById(R.id.action_order);

        //WARNING: set up on touch listener to check whetehr any change were made before leaving the page and warn them if necessary
        productNameEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneNumberEditText.setOnTouchListener(touchListener);
        inStockSpinner.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        increaseButton.setOnTouchListener(touchListener);
        decreaseButton.setOnTouchListener(touchListener);


        //set up increase quantity button
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuantity();
            }
        });
        //set up decrease quantity button
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when the delete button is pressed, we want a dialog to appear and confirm the action.
                showDeleteConfirmationDialog();
            }
        });

        //Set up call action button to call supplier. If there is no phone number in the DB, DIAL allows you to dial or change the given phone number.
        orderProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhoneNumber = supplierPhoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                startActivity(intent);
            }
        });

        setupSpinner();

    }

    /**
     * QUANTITY BUTTON: when pressed it removes or add one to the quantity number. The quantity cannot go under 0
     */

    private void addQuantity() {
        String quantityString = quantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) {
            quantityString = "0";
        }
        int quantity = Integer.parseInt(quantityString);
        quantity = quantity + 1;
        quantityEditText.setText(String.valueOf(quantity));
    }

    private void decreaseQuantity() {
        String quantityString = quantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) {
            quantityString = "0";
        }
        int quantity = Integer.parseInt(quantityString);
        if (quantity == 0) {
            Toast.makeText(this, getString(R.string.editor_quantity_negative_error_msg), Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity = quantity - 1;
        }
        quantityEditText.setText(String.valueOf(quantity));
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

    private void save() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String productNameString = productNameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = supplierPhoneNumberEditText.getText().toString().trim();

        if ((TextUtils.isEmpty(productNameString)) || (TextUtils.isEmpty(priceString) || (TextUtils.isEmpty(quantityString) || (TextUtils.isEmpty(supplierNameString) || (TextUtils.isEmpty(supplierPhoneNumber)))))) {

            Toast.makeText(this, getString(R.string.missing_info_for_successful_save), Toast.LENGTH_LONG).show();

            return;
        }
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

        } else {
            //insert a new row in the table with a specific ID
            int update = getContentResolver().update(currentUri, values, null, null);
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            if (update == 0) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();

            }
        }


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
                //no changes have been made the user can go back
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask((EditActivity.this));
                    return true;
                }

                //changes have been made, a dialog interface is triggered
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user clicks "discard"
                        NavUtils.navigateUpFromSameTask(EditActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * LOADER to communicate with the database and display information to be updated
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {BookEntry._ID, BookEntry.COLUMN__PRODUCT_NAME, BookEntry.COLUMN_PRICE, BookEntry.COLUMN_IN_STOCK, BookEntry.COLUMN_QUANTITY, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};


        //this loader will execute the content provider 's query method in the background.
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, currentUri,             //the specific uri called
                projection,             //the column for the WHERE cl
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        if (cursorData.moveToFirst()) {
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
            final String phoneNumber = cursorData.getString(phoneColumnIndex);

            //display information from the current product
            productNameEditText.setText(product);
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
            supplierNameEditText.setText(supplier);
            supplierPhoneNumberEditText.setText(phoneNumber);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //LEAVING THE EDIT PAGE WITHOUT SAVING INFORMATION
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder warning = new AlertDialog.Builder(this);
        warning.setMessage(R.string.unsaved_changed_dialog_msg);
        warning.setPositiveButton(R.string.discard_msg, discardButtonClickListener);
        warning.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = warning.create();
        alertDialog.show();
    }


    //When back button is pressed, this method is called and check whether there are unsaved informaion
    @Override
    public void onBackPressed() {
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //in case of discard, this will finish the activity
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    ////DELETE///

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button and trigger the delete method.
                delete();
                //once the product is deleted, the user is sent back to the main activity but stays if he/she pressed "cancel"
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void delete() {
        if (currentUri != null) {
            int delete = getContentResolver().delete(currentUri, null, null);
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            if (delete == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();

            }
        }

    }

}
