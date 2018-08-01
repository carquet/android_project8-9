package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.security.Provider;

import com.example.android.inventoryapp.data.BookContract.BookEntry;


public class InventoryProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /** initialize the dbhelper*/
    private InventoryDbHelper dbHelper;

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 100;

    /** URI matcher code for the content URI for a single book in the books table */
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
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS );
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS+"/#", BOOK_ID);
    }

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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        //open the database in reading mode
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //this is the cursor that will return the result of the method in each case
        Cursor cursor;

        //adapt the results according to the uri sent
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);
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
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //adapt the results according to the uri sent
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBooks(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertBooks(Uri uri, ContentValues contentValues) {

        // SANITY CHECK: checks that the value entered by the user are valid.
        String productName = contentValues.getAsString(BookEntry.COLUMN__PRODUCT_NAME);
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("the product cannot be null");
        }

        Integer inStock = contentValues.getAsInteger(BookEntry.COLUMN_IN_STOCK);
        if ((inStock != BookEntry.IN_STOCK) && (inStock != BookEntry.NOT_IN_STOCK)){
            throw new IllegalArgumentException("The product requires to be either in stock or not in stock");
        }

        Integer productPrice = contentValues.getAsInteger(BookEntry.COLUMN_PRICE);
        if ((productPrice == null) || (productPrice < 0)){
            throw new IllegalArgumentException("the price needs to be a positive number");
        }

        Integer quantity = contentValues.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if (quantity < 0){
            throw new IllegalArgumentException("the quantity needs to be a positive number");
        }

        // No need to check the supplier name and its phone number, any value is valid (including null).

        //open the DB in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                //Extract the ID form the uri
                selection = BookEntry._ID + "+?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Updateion is not supported for " + uri);

        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // SANITY CHECK: checks that the value entered by the user are valid.
        if(contentValues.containsKey(BookEntry.COLUMN__PRODUCT_NAME)){
            String productName = contentValues.getAsString(BookEntry.COLUMN__PRODUCT_NAME);
            if (productName == null || productName.isEmpty()) {
                throw new IllegalArgumentException("the product cannot be null");
            }
        }

        if(contentValues.containsKey(BookEntry.COLUMN_IN_STOCK)){
            Integer inStock = contentValues.getAsInteger(BookEntry.COLUMN_IN_STOCK);
            if ((inStock != BookEntry.IN_STOCK) && (inStock != BookEntry.NOT_IN_STOCK)){
                throw new IllegalArgumentException("The product requires to be either in stock or not in stock");
            }
        }

        if(contentValues.containsKey(BookEntry.COLUMN_PRICE)){
            Integer productPrice = contentValues.getAsInteger(BookEntry.COLUMN_PRICE);
            if ((productPrice == null) || (productPrice < 0)){
                throw new IllegalArgumentException("the price needs to be a positive number");
            }
        }

        if(contentValues.containsKey(BookEntry.COLUMN_QUANTITY)){
            Integer quantity = contentValues.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity < 0){
                throw new IllegalArgumentException("the quantity needs to be a positive number");
            }
        }

        if(contentValues.size() == 0){
            return 0;
        }
        // No need to check the supplier name and its phone number, any value is valid (including null).

        //open the DB in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();


        // Returns the number of database rows affected by the update statement
        return database.update(BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
