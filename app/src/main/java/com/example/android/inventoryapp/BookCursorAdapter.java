package com.example.android.inventoryapp;

import android.content.ContentUris;
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
        float productPriceFloat = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));
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
                //Uri currentProduct = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, productId);
                Toast.makeText(context, String.valueOf(productId), Toast.LENGTH_SHORT).show();

            }
        });

    }



}
