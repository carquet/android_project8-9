package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

//this should be in a data package inside the app package
public final class BookContract {

    //CREATE TABLE books(product_name, price, in_stock,quantity,supplier_name,supplier_phone_number)

    public static abstract class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final String COLUMN__PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_IN_STOCK = "in_stock";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * Possible values for the product being in stock.
         */
        public static final int IN_STOCK = 0;
        public static final int NOT_IN_STOCK = 1;

    }
}