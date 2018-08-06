package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//this should be in a data package inside the app package
public final class BookContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventoryapp/books/ is a valid path for
     * looking at book data. content://com.example.android.inventoryapp/library/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "library".
     */
    public static final String PATH_BOOKS = "books";

    //CREATE TABLE books(product_name, price, in_stock,quantity,supplier_name,supplier_phone_number)

    public static abstract class BookEntry implements BaseColumns {
        /** The content URI to access the book data in the provider
         * Uri object with withAppendedPath method: Creates a new Uri by appending an already-encoded path segment to a base Uri.
         * */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


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
        public static final int IN_STOCK = 1;
        public static final int NOT_IN_STOCK = 0;

    }
}