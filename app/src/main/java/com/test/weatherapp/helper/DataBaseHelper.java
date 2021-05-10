package com.test.weatherapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.test.weatherapp.data.model.CityInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather.db";
    /*Login Table Structure*/
    private static final String TABLE_CITY_INFORMATION = "CityInformation";
    private static final String CITY_NAME = "city_name";
    private static final String CITY_COUNTRY = "city_country";
    private static final String ROW_ID = "rowid";

    public static final String CREATE_TABLE_CITY_LIST = "CREATE TABLE "
            + TABLE_CITY_INFORMATION + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CITY_NAME + " TEXT, " + CITY_COUNTRY + " TEXT )";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITY_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.i("tag", oldVersion +" newVersion "+newVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Insert Api List Data for Api List Table*/
    public boolean insertCityListData(String cityName, String cityCountry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CITY_NAME, cityName);
        contentValues.put(CITY_COUNTRY, cityCountry);
        db.insert(TABLE_CITY_INFORMATION, null, contentValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public ArrayList<CityInfo> cityData() {
        ArrayList<CityInfo> cityInfoArrayList = new ArrayList<>();
        cityInfoArrayList.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + TABLE_CITY_INFORMATION , null);
            if (cursor.moveToFirst()) {
                do {
                    String cityName = cursor.getString(cursor.getColumnIndex(CITY_NAME));
                    String cityCountry = cursor.getString(cursor.getColumnIndex(CITY_COUNTRY));
                    int id = cursor.getInt(cursor.getColumnIndex(ROW_ID));

                    CityInfo cityInfo = new CityInfo();
                    cityInfo.setName(cityName);
                    cityInfo.setCountry(cityCountry);
                    cityInfo.setId(id);
                    cityInfoArrayList.add(cityInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }finally {
            cursor.close();
        }
        return cityInfoArrayList;
    }

    public boolean cityExists(String cityName, String cityCountry) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exist = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + TABLE_CITY_INFORMATION + " where " + CITY_NAME + "=\"" + cityName + "\"  AND " + CITY_COUNTRY + "=\"" +cityCountry +"\"", null);
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }finally {
            cursor.close();
        }

        return exist;
    }

}



