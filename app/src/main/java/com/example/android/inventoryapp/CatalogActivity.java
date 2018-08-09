package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    InventoryDbHelper mDbHelper;
    BookCursorAdapter cursorAdapter;
    int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Fb button with explicit intent to the edit page for new product
        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(editIntent);
            }
        });

        //SET UP THE EMPTY VIEW
        //1. Grab the books that is going to be populated
        ListView booksListView = findViewById(R.id.text_view_books);
        //2. set the empty view on the listView
        View emptyView = findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyView);

        //HOOK ADAPTER TO LISTVIEW
        booksListView = (ListView) findViewById(R.id.text_view_books);
        cursorAdapter = new BookCursorAdapter(this, null);
        booksListView.setAdapter(cursorAdapter);

        //hook an intent on the itemView clicked that opens an edit page for update
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent updateEditIntent = new Intent(CatalogActivity.this, EditActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                updateEditIntent.setData(currentUri);
                startActivity(updateEditIntent);
            }
        });



        //ASYNC TASK
        //1. setting up the loader and launching it --> 2. override methods: onCreateLoader, onLoadFinish, onLoaderReset
        // specific ID attached to this loader
        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);


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
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Cambridge");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "555-345-234");

        //insert a new row in the table with a specific ID
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        /*if ((newUri == null) || newUri.equals(Uri.EMPTY)) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();
        }*/

    }

    /**
     * MENU
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
                return true;
            case R.id.action_delete_all:
                //when the delete button is pressed, we want a dialog to appear and confirm the action.
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * LOADER
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Defines a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN__PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_IN_STOCK,
                BookEntry.COLUMN_QUANTITY};
                //BookEntry.COLUMN_SUPPLIER_NAME,
                //BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        //this loader will excute the content provider 's query method in the background.
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this,
                BookEntry.CONTENT_URI, //the table to the query
                projection,             //the column for the WHERE clause
                null,           //the value for the WHERE clause
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor currentData) {
        //fill the adapter with updated information
        cursorAdapter.swapCursor(currentData);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //clean the adapter of all data
        cursorAdapter.swapCursor(null);

    }

    ////DELETE///

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button and trigger the deleteAll method.
                deleteAll();
                //once the product is deleted, the user is sent back to the main activity but stays if he/she pressed "cancel"
                //finish();
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
    private void deleteAll() {
        if(BookEntry.CONTENT_URI != null){
            //insert a new row in the table with a specific ID
            int delete = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            if (delete == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_all_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_all_product_successful), Toast.LENGTH_SHORT).show();

            }
        }

    }
}
