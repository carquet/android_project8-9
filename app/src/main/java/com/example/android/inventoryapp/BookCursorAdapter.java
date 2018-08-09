package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract;
import com.example.android.inventoryapp.data.BookContract.BookEntry;


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
     *                Flags used to determine the behavior of the adapter; recommended constructor
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
        /**DISPLAY INFORMATION WHEN UPDATING ON EDIT PAGE*/

        // FIND fields to populate in inflated template
        TextView productNameView = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceView = (TextView) view.findViewById(R.id.product_price);
        TextView stockView = (TextView) view.findViewById(R.id.stock);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.action_sale);
        TextView id = (TextView) view.findViewById(R.id.id);
        //Supplier name and phone number are not needed in the UI at the moment

        // EXTRACT properties from cursor
        final int productId = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));
        String productNameString = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN__PRODUCT_NAME));
        final float productPriceFloat = cursor.getFloat(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRICE));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_IN_STOCK));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));

        // POPULATE fields with extracted properties
        //NAME of product
        productNameView.setText(productNameString);
        //PRICE of product
        productPriceView.setText(String.valueOf(productPriceFloat));
        //STOCK of product
        if (stock == 0) {
            stockView.setText(R.string.not_in_stock);
        } else {
            stockView.setText(R.string.in_stock);
        }
        //QUANTITY of product
        quantityView.setText(String.valueOf(quantity));
        //Supplier name and phone number are not needed at the moment in the UI


        /**SALE BUTTON
         * Clicking on the button will reduce the current product's quantity number by one and insert the new quantity into the DB of the same product*/
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take the current product's displayed price and remove 1
                //check that the current quantity hasn't reached 0. If it has, prevent the user from substracting any further.
                if (quantity == 0) {
                    Toast.makeText(context, context.getString(R.string.editor_quantity_negative_error_msg), Toast.LENGTH_LONG).show();
                } else {
                    int currentProductQuantityMinusOne = quantity;
                    currentProductQuantityMinusOne--;

                    //Put the currentProductPriceReduced into the DB in the correct column
                    ContentValues quantityValues = new ContentValues();
                    quantityValues.put(BookContract.BookEntry.COLUMN_QUANTITY, currentProductQuantityMinusOne);

                    //insert the values of the quantity column and its value in the correct table and row
                    Uri currentProduct = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, productId);
                    int update = context.getContentResolver().update(currentProduct, quantityValues, null, null);
                    //if not updated, the toast will display 0 row updated
                    if (update == 0) {
                        Toast.makeText(context, context.getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.editor_update_book_success), Toast.LENGTH_SHORT).show();
                        context.getContentResolver().notifyChange(currentProduct, null);
                    }
                }

            }
        }); //end of SALE BUTTON

    } //end of bindView

}
