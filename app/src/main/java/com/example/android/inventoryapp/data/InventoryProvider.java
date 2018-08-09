package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;


public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * initialize the dbhelper
     */
    private InventoryDbHelper dbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //open the database in reading mode
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //this is the cursor that will return the result of the method in each case
        Cursor cursor;

        //adapt the results according to the uri sent
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case BOOK_ID:
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the _id equals a integer given to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unknown_uri_error_msg) + uri);
        }
        // set the notification on the cursor: it checks whether there was any change made on the specified uri.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //adapt the results according to the uri sent
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBooks(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unknown_uri_error_msg) + uri);
        }
    }

    private Uri insertBooks(Uri uri, ContentValues contentValues) {
        // SANITY CHECK: checks before inserting into the database that the values entered by the user are valid
        String productName = contentValues.getAsString(BookEntry.COLUMN__PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_null_product_name_error_msg));
        }

        Integer inStock = contentValues.getAsInteger(BookEntry.COLUMN_IN_STOCK);
        if ((inStock != BookEntry.IN_STOCK) && (inStock != BookEntry.NOT_IN_STOCK)) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_null_stock_error_msg));
        }

        Integer productPrice = contentValues.getAsInteger(BookEntry.COLUMN_PRICE);
        if (productPrice != null && productPrice < 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_negative_price_error_msg));
        }

        Integer quantity = contentValues.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.editor_quantity_negative_error_msg));
        }

        // No need to check the supplier name and its phone number, any value is valid (including null).

        //once the values are checked, open the DB in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Insert the new book with the given values(contentValues, and return the integer representing the row
        long id = database.insert(BookEntry.TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else {
            //notify changes so that the UI can update with the new information
            getContext().getContentResolver().notifyChange(uri, null);

            return ContentUris.withAppendedId(uri, id);
        }

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                //Update only one specific row: Extract the ID form the uri
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unknown_uri_error_msg) + uri);

        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // SANITY CHECK: checks before updating the database with the new values entered by the user.
        if (contentValues.containsKey(BookEntry.COLUMN__PRODUCT_NAME)) {
            String productName = contentValues.getAsString(BookEntry.COLUMN__PRODUCT_NAME);
            if (productName == null || productName.isEmpty()) {
                throw new IllegalArgumentException(getContext().getString(R.string.no_null_product_name_error_msg));
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_IN_STOCK)) {
            Integer inStock = contentValues.getAsInteger(BookEntry.COLUMN_IN_STOCK);
            if ((inStock != BookEntry.IN_STOCK) && (inStock != BookEntry.NOT_IN_STOCK)) {
                throw new IllegalArgumentException(getContext().getString(R.string.no_null_stock_error_msg));
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_PRICE)) {
            Integer productPrice = contentValues.getAsInteger(BookEntry.COLUMN_PRICE);
            if ((productPrice == null) || (productPrice < 0)) {
                throw new IllegalArgumentException(getContext().getString(R.string.no_negative_price_error_msg));
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException(getContext().getString(R.string.editor_quantity_negative_error_msg));
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)){
            String supplierName = contentValues.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException(getContext().getString(R.string.no_null_supplier_name_error_msg));
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)){
            String supplierPhoneNumber = contentValues.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierPhoneNumber)) {
                throw new IllegalArgumentException(getContext().getString(R.string.no_null_supplier_phone_error_msg));
            }
        }


        //NOTIFY: checks that any change has been made
        if (contentValues.size() == 0) {
            return 0;
        }

        //open the DB in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //enter the new info
        int updateBookUri = database.update(BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (updateBookUri != 0) {//notify for the front end to update
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return updateBookUri;


    }

    /**
     * Delete the data at the given selection and selection arguments. Since the code is rather small, there won't be a separate method.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //since there is no sanity check and no information to insert, the code is kept here instead of two separate methods
        final int match = sUriMatcher.match(uri);

        //match the received uri with a specific action
        switch (match) {
            case BOOKS: //delete all rows
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) { //if the integer returned is 1 or more, it means that one or more rows have been deleted.
                    //NOTIFY to update the front
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted; //returns the number of rows deleted
            case BOOK_ID:
                // Delete ONE ITEM: extract the ID from the Uri
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.deletion_no_supported_error_msg) + uri);
        }
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
