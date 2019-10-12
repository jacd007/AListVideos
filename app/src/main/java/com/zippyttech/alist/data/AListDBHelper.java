package com.zippyttech.alist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zippyttech on 21/08/17.
 */

public class AListDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AList.db";
    public Context context;


    public AListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AListSchedule.SQL_CREATE_PRODUCT);
        sqLiteDatabase.execSQL(AListSchedule.SQL_CREATE_IMAGES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old, int i1) {
//       if(old<1){
//           sqLiteDatabase.execSQL(AListSchedule.ALTER_TABLES_COMPANY_ADD_BALANCE);
//           sqLiteDatabase.execSQL(AListSchedule.ALTER_TABLES_COMPANY_ADD_DEBT);
//
//       }

    }
}
