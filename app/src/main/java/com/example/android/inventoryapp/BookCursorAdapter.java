package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract;

/**
 * * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */

public class BookCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     * Flags used to determine the behavior of the adapter; recommended constructor
     */
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    // The newView method is used to inflate a new view and return it,
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView productNameView = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceView = (TextView) view.findViewById(R.id.product_price);
        TextView stockView = (TextView) view.findViewById(R.id.stock);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.action_sale);
        TextView id = (TextView) view.findViewById(R.id.id);

        //Supplier name and phone number are not needed in the UI at the moment
        //TextView supplierNameView = (TextView) view.findViewById(R.id.supplier_name);
        //TextView phoneNumberView = (TextView) view.findViewById(R.id.supplier_phone_number);

        // Extract properties from cursor
        final int productId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String productNameString = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        final float productPriceFloat = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow("in_stock"));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));



        // Populate fields with extracted properties
        //Name of product
        productNameView.setText(productNameString);
        //price of product
        productPriceView.setText(String.valueOf(productPriceFloat));
        //stock of product
        if (stock == 0) {
            stockView.setText(R.string.not_in_stock);
        } else {
            stockView.setText(R.string.in_stock);
        }
        //quantity of product
        quantityView.setText(String.valueOf(quantity));

        //Supplier name and phone number are not needed at the moment in the UI
        /*if(TextUtils.isEmpty(supplierName)){
            supplierNameView.setText(R.string.supplier_unknown);
        }else{
            supplierNameView.setText(supplierName);
        }

        if(TextUtils.isEmpty(supplierName)){
            phoneNumberView.setText(R.string.supplier_phonenumber_unknown);
        }else{
            phoneNumberView.setText(supplierPhoneNumber);
        }*/

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take the current product's displayed price and remove 1
                float currentProductQuantityMinusOne = quantity;
                currentProductQuantityMinusOne--;
                //Put the currentProductPriceReduced into the DB in the correct column
                Uri currentProduct = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, productId);
                Toast.makeText(context, String.valueOf(currentProductQuantityMinusOne), Toast.LENGTH_SHORT).show();
                //defines an object to put the values
                ContentValues quantityValues = new ContentValues();
                quantityValues.put(BookContract.BookEntry.COLUMN_QUANTITY, currentProductQuantityMinusOne);

                //insert the updated information into its correct column
                int update = context.getContentResolver().update(currentProduct, quantityValues, null, null);
                //if not updated, the toast will display 0 row updated
                if (update == 0) {
                    Toast.makeText(context, "cest pas gagné", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "cest dans la poche", Toast.LENGTH_SHORT).show();

                }


                /*//chekcs if it is a new product or an existing one by checking the existence of the uri
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
*/

            }
        });

    }



}
