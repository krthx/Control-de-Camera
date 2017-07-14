package com.krthx.cameraapplication.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bryam on 13/07/17.
 */
@SuppressWarnings("ALL")
public class DbImageManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ImageManager.db";
    public static final String SPLASH_TABLE_NAME = "ImageManager";

    private HashMap hp;

    public DbImageManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + SPLASH_TABLE_NAME + "( name TEXT, image BLOB)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertImage(String name, Bitmap img) {
        Bitmap storedBitmap = null;
        String sql = "INSERT INTO " + SPLASH_TABLE_NAME + " (name,image) VALUES(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement insertStmt = db.compileStatement(sql);

        byte[] imgByte = getBitmapAsByteArray(img);
        try {
            storedBitmap = getImage(name);
        } catch (Exception e) {
            //AppLog.exception(e);
        }
        if (storedBitmap == null) {
            insertStmt.bindString(1, name);
            insertStmt.bindBlob(2, imgByte);
            insertStmt.executeInsert();
            db.close();
        }
        return true;
    }


    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SPLASH_TABLE_NAME);
        return numRows;
    }

    public Bitmap getImage(String name) {
        String qu = "SELECT * FROM " + SPLASH_TABLE_NAME;
        Cursor cur = null;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            cur = db.rawQuery(qu, new String[]{});
        } catch (Exception e) {
            //AppLog.exception(e);
        }
        if (cur != null) {
            if (cur.moveToFirst()) {
                int index = cur.getColumnIndexOrThrow("image");
                byte[] imgByte = cur.getBlob(index);
                cur.close();
                return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            }
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return null;
    }

    public List<Bitmap> getImages() {
        String qu = "SELECT * FROM " + SPLASH_TABLE_NAME;
        Cursor cur = null;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Bitmap> images = new ArrayList<>();

        try {
            cur = db.rawQuery(qu, new String[]{});
        } catch (Exception e) {
            //AppLog.exception(e);
        }
        if (cur != null) {
            if (cur.moveToFirst()) {
                int index = cur.getColumnIndexOrThrow("image");
                byte[] imgByte = cur.getBlob(index);
                cur.close();
                images.add(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
            }
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return images;
    }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        return outputStream.toByteArray();
    }
}