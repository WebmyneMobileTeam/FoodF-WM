package foodbazar.webmyne.com.foodbaazar.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import foodbazar.webmyne.com.foodbaazar.model.CartPojo;
import foodbazar.webmyne.com.foodbaazar.model.ExtraPojo;
import foodbazar.webmyne.com.foodbaazar.model.MenuTypeItemstrans;

/**
 * Created by jaydeeprana on 01-07-2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String KEY_ROWID = "_id";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "foodfad.db";
    private static final String TABLE_CART_ITEM = "Cart";
    private static final String TABLE_EXTRAS = "Extras";

    private static final String DATABASE_PATH = "/data/data/foodbazar.webmyne.com.foodbaazar/databases/";
    private Context context;
    private SQLiteDatabase myDataBase = null;

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        if (dbExist) {
//            Log.v("log_tag", "database does exist");
        } else {
//            Log.v("log_tag", "database does not exist");
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDataBase() {

        File folder = new File(DATABASE_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    public boolean openDataBase() throws SQLException {
        String mPath = DATABASE_PATH + DATABASE_NAME;
        //Log.v("mPath", mPath);
        myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return myDataBase != null;

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    // Constructor
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteCart() {
        myDataBase = this.getWritableDatabase();
        myDataBase.delete(TABLE_CART_ITEM, null, null);
        myDataBase.delete(TABLE_EXTRAS, null, null);
        myDataBase.close();
    }

    public void addCartProduct(MenuTypeItemstrans menuTypeItemstrans, int quantity, int restaurantID, String itemName, ArrayList<Integer> extraIDs, double tax, String imgPath) {
        myDataBase = this.getWritableDatabase();

        //removeItem(menuTypeItemstrans.PriceID);
        String uuid = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        values.put("item_name", itemName);
        values.put("item_price", menuTypeItemstrans.Price);
        values.put("quantity", quantity);
        values.put("item_type_id", menuTypeItemstrans.TypeID);
        values.put("item_type_name", menuTypeItemstrans.TypeName);
        values.put("restaurant_id", restaurantID);
        values.put("price_id", menuTypeItemstrans.PriceID);
        values.put("restaurant_tax", tax);
        values.put("image_path", imgPath);

        ArrayList<Integer> unique = new ArrayList<>();
        unique.add(menuTypeItemstrans.PriceID);

        int total = menuTypeItemstrans.Price * quantity;
        int extraPrice = 0;

        for (int i = 0; i < menuTypeItemstrans.Extras.size(); i++) {

            for (int j = 0; j < extraIDs.size(); j++) {

                if (menuTypeItemstrans.Extras.get(i).ExtraItemID == extraIDs.get(j)) {

                    ContentValues values1 = new ContentValues();
                    values1.put("item_type_id", menuTypeItemstrans.TypeID);
                    values1.put("uuid", uuid);
                    values1.put("extra_id", menuTypeItemstrans.Extras.get(i).ExtraItemID);
                    values1.put("extra_name", menuTypeItemstrans.Extras.get(i).ExtraName);
                    values1.put("price_id", menuTypeItemstrans.PriceID);
                    values1.put("extra_price", menuTypeItemstrans.Extras.get(i).Price);
                    extraPrice += menuTypeItemstrans.Extras.get(i).Price;
                    unique.add(menuTypeItemstrans.Extras.get(i).ExtraItemID);
                    myDataBase.insert(TABLE_EXTRAS, null, values1);

                }
            }
        }

        Collections.sort(unique);
        String concatStr = Functions.concatValues(unique);
        // Log.e("concat_db", concatStr);

        values.put("unique_id", concatStr);

        extraPrice = extraPrice * quantity;

//        values.put("total_price", total + extraPrice);
        values.put("total_price", total + extraPrice);
        myDataBase.insert(TABLE_CART_ITEM, null, values);
    }

    public void removeItem(String uuId) {
        myDataBase = this.getWritableDatabase();
        String selectQuery = "DELETE FROM " + TABLE_CART_ITEM + " WHERE uuId ='" + uuId + "'";
        myDataBase.execSQL(selectQuery);

        String selectQuery1 = "DELETE FROM " + TABLE_EXTRAS + " WHERE uuId ='" + uuId + "'";
        myDataBase.execSQL(selectQuery1);
    }

    public boolean isExists(int priceID) {
        boolean available = false;
        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT * FROM " + TABLE_CART_ITEM + " WHERE price_id =" + priceID;
        cursor = myDataBase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            available = true;
            cursor.moveToFirst();
        }
        return available;
    }

    public void update(int priceID, int quantity, int price) {
        myDataBase = this.getWritableDatabase();

        int oldQTY = getOldQty(priceID);

        quantity += oldQTY;
        int totalPrice = price * quantity;
        String selectQuery = "UPDATE " + TABLE_CART_ITEM + " SET quantity=" + quantity + ", total_price=" + totalPrice + " WHERE price_id ='" + priceID + "'";
        myDataBase.execSQL(selectQuery);
    }

    private int getOldQty(int priceID) {
        int old = 0;

        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT quantity FROM " + TABLE_CART_ITEM + " WHERE price_id =" + priceID;
        cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                old = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            } while (cursor.moveToNext());
        }
        return old;
    }

    public String getUniqueId(int priceID) {
        String uniqueId = "";
        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT unique_id FROM " + TABLE_CART_ITEM + " WHERE price_id ='" + priceID + "'";
        cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                uniqueId = cursor.getString(cursor.getColumnIndexOrThrow("unique_id"));
            } while (cursor.moveToNext());
        }
        return uniqueId;
    }

    public void updateWithExtra(MenuTypeItemstrans menuTypeItemstrans, int quantity, ArrayList<Integer> extraIDs, String oldUnique) {
        myDataBase = this.getWritableDatabase();

        int oldQTY = getOldQty(menuTypeItemstrans.PriceID);
        int extraPrice = 0;

        quantity += oldQTY;
        int totalPrice = menuTypeItemstrans.Price * quantity;

        for (int i = 0; i < menuTypeItemstrans.Extras.size(); i++) {

            for (int j = 0; j < extraIDs.size(); j++) {

                if (menuTypeItemstrans.Extras.get(i).ExtraItemID == extraIDs.get(j)) {
                    extraPrice += menuTypeItemstrans.Extras.get(i).Price;

                }
            }
        }

        extraPrice = extraPrice * quantity;

        totalPrice += totalPrice + extraPrice;

        String selectQuery = "UPDATE " + TABLE_CART_ITEM + " SET quantity=" + quantity + ", total_price=" + totalPrice + " WHERE unique_id ='" + oldUnique + "'";
        myDataBase.execSQL(selectQuery);
    }

    public List<CartPojo> getCartPojos() {
        List<CartPojo> cartPojos = new ArrayList<>();

        myDataBase = this.getWritableDatabase();
        Cursor cursor = null;

        String selectQuery = "SELECT * FROM " + TABLE_CART_ITEM;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        //Log.e("cursor count", cursor.getCount() + "");

        if (cursor.getCount() > 0) {
            do {
                CartPojo cart = new CartPojo();
                cart.setUniqueId(cursor.getString(cursor.getColumnIndexOrThrow("unique_id")));

                String uuId = cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
                cart.setUuId(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                cart.setItemTypeName(cursor.getString(cursor.getColumnIndexOrThrow("item_type_name")));
                cart.setItemName(cursor.getString(cursor.getColumnIndexOrThrow("item_name")));
                cart.setPriceId(cursor.getInt(cursor.getColumnIndexOrThrow("price_id")));
                cart.setTotalPrice(cursor.getInt(cursor.getColumnIndexOrThrow("total_price")));

                cart.setItemPrice(cursor.getInt(cursor.getColumnIndexOrThrow("item_price")));
                cart.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                cart.setItemTypeId(cursor.getInt(cursor.getColumnIndexOrThrow("item_type_id")));
                cart.setRestaurantId(cursor.getInt(cursor.getColumnIndexOrThrow("restaurant_id")));
                cart.setTax(cursor.getDouble(cursor.getColumnIndexOrThrow("restaurant_tax")));
                cart.setImgPath(cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
                //Log.e("uuId", uuId);
                cart.setExtraPojos(getExtras(uuId));

                cartPojos.add(cart);
            } while (cursor.moveToNext());
        }

        return cartPojos;
    }

    private List<ExtraPojo> getExtras(String uuId) {
        List<ExtraPojo> extras = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery1 = "SELECT * FROM " + TABLE_EXTRAS + " WHERE uuid ='" + uuId + "'";
        cursor = myDataBase.rawQuery(selectQuery1, null);
        cursor.moveToFirst();

        //Log.e("cursor", cursor.getCount() + "---");

        if (cursor.getCount() > 0) {
            do {
                ExtraPojo extraObj = new ExtraPojo();
                extraObj.setExtraName(cursor.getString(cursor.getColumnIndexOrThrow("extra_name")));
                extraObj.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                extraObj.setPriceId(cursor.getInt(cursor.getColumnIndexOrThrow("price_id")));
                extraObj.setExtraPrice(cursor.getInt(cursor.getColumnIndexOrThrow("extra_price")));

                extraObj.setItemTypeId(cursor.getInt(cursor.getColumnIndexOrThrow("item_type_id")));
                extraObj.setExtraId(cursor.getInt(cursor.getColumnIndexOrThrow("extra_id")));

                extras.add(extraObj);
            } while (cursor.moveToNext());
        }
        return extras;
    }

    public void updateQuantity(int quantity, String uuId, int priceId) {
        myDataBase = this.getWritableDatabase();

        int totalPrice = getOldTotalPrice(uuId);
        int oldQty = getOldQty(priceId);
        int singleTotalPrice = totalPrice / oldQty;
        int finalTotalPrice = singleTotalPrice * quantity;

        String selectQuery = "UPDATE " + TABLE_CART_ITEM + " SET quantity=" + quantity + ", total_price=" + finalTotalPrice + " WHERE uuId ='" + uuId + "'";
        myDataBase.execSQL(selectQuery);
    }

    private int getOldTotalPrice(String uuId) {
        int oldTotal = 0;
        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT total_price FROM " + TABLE_CART_ITEM + " WHERE uuId ='" + uuId + "'";
        cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                oldTotal = cursor.getInt(cursor.getColumnIndexOrThrow("total_price"));
            } while (cursor.moveToNext());
        }
        return oldTotal;
    }

    public boolean checkIfAdded(int restaurantID) {

        boolean canAdd = false;
        myDataBase = this.getWritableDatabase();
        Cursor cursor = null;
        int restId = 0;

        String selectQuery = "SELECT * FROM " + TABLE_CART_ITEM;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                restId = cursor.getInt(cursor.getColumnIndexOrThrow("restaurant_id"));
            } while (cursor.moveToNext());

            if (restId == restaurantID) {
                canAdd = true;

            } else {
                canAdd = false;
            }
        } else {
            canAdd = true;
        }

        return canAdd;
    }
}
