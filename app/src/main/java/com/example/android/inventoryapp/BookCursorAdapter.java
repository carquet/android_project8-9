package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView productNameView = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceView = (TextView) view.findViewById(R.id.product_price);
        TextView stockView = (TextView) view.findViewById(R.id.stock);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);
        TextView supplierNameView = (TextView) view.findViewById(R.id.supplier_name);
        TextView phoneNumberView = (TextView) view.findViewById(R.id.supplier_phone_number);

        // Extract properties from cursor
        String productNameString = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        float productPriceFloat = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow("in_stock"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        String supplierName = cursor.getString(cursor.getColumnIndexOrThrow("supplier_name"));
        String supplierPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("supplier_phone_number"));


        // Populate fields with extracted properties
        productNameView.setText(productNameString);
        productPriceView.setText(String.valueOf(productPriceFloat));
        if (stock == 0) {
            stockView.setText(R.string.not_in_stock);
        } else {
            stockView.setText(R.string.in_stock);
        }
        quantityView.setText(String.valueOf(quantity));
        //see issue #1 next iteration: set an empty field if there is no information
        supplierNameView.setText(supplierName);
        phoneNumberView.setText(supplierPhoneNumber);

    }


}
