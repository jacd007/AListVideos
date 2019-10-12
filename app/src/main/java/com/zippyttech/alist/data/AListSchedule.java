package com.zippyttech.alist.data;

import android.provider.BaseColumns;

/**
 * Created by zippyttech on 21/08/17.
 */

public class AListSchedule {


    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    /**
     * Create tables of data base
     */
    public static final String SQL_CREATE_PRODUCT =
            "CREATE TABLE " +  Videos.TABLE_NAME + " (" +
                    Videos._ID + " INTEGER PRIMARY KEY," +
                    Videos.COLUMN_NAME_ID + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_CAPITULE + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_ID_STAT + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_ID_DATA + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_ID_DATA_UPDATE + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_ID_TYPE + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_ID_DAY + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_ID_COLOR + INTEGER_TYPE  +  COMMA_SEP +

                    Videos.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_DATA + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_DATA_UPDATE + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_COLOR + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_DAY + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_OTHERS + TEXT_TYPE + COMMA_SEP +

                    Videos.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_IMAGE_DIR + TEXT_TYPE + COMMA_SEP +
                    Videos.COLUMN_NAME_TAG + TEXT_TYPE +
                    ")";

    public static final String SQL_CREATE_IMAGES=
            "CREATE TABLE " +  Images.TABLE_NAME + " (" +
                    Images._ID + " INTEGER PRIMARY KEY," +
                    Images.COLUMN_ID_IMAGE + INTEGER_TYPE  +  COMMA_SEP +
                    Images.COLUMN_NAME_IMAGE + TEXT_TYPE  + COMMA_SEP +
                    Images.COLUMN_NAME_TITLE + TEXT_TYPE  +
                    ")";

//    public static final String ALTER_TABLES_COMPANY_ADD_DEBT = "" +
//            "ALTER TABLE "+Product.TABLE_NAME+" ADD COLUMN "+Product.COLUMN_NAME_DEBT+INTEGER_TYPE+" DEFAULT 0";
//
//    public static final String ALTER_TABLES_COMPANY_ADD_BALANCE = "" +
//            "ALTER TABLE "+Product.TABLE_NAME+" ADD COLUMN "+Product.COLUMN_NAME_BALANCE+INTEGER_TYPE+" DEFAULT 0";
    public static abstract class Videos implements BaseColumns {

        public static final String TABLE_NAME = "Video";

        public static final String COLUMN_NAME_ID= "itemId";
        public static final String COLUMN_CAPITULE = "itemCapitule";
        public static final String COLUMN_ID_STAT= "idStat";
        public static final String COLUMN_ID_DATA= "idData";
        public static final String COLUMN_ID_DATA_UPDATE= "idDataUp";
        public static final String COLUMN_ID_TYPE= "idType";
        public static final String COLUMN_ID_DAY= "idDay";
        public static final String COLUMN_ID_COLOR= "idColor";

        public static final String COLUMN_TITLE= "itemTitle";
        public static final String COLUMN_NAME_DATA= "itemData";
        public static final String COLUMN_NAME_DATA_UPDATE= "itemDataUp";
        public static final String COLUMN_NAME_STATUS= "itemStat";
        public static final String COLUMN_COLOR= "itemColor";
        public static final String COLUMN_NAME_DAY= "itemDay";
        public static final String COLUMN_NAME_TYPE= "itemType";
        public static final String COLUMN_NAME_OTHERS= "itemOthers";

        public static final String COLUMN_NAME_IMAGE= "itemImage64";
        public static final String COLUMN_NAME_IMAGE_DIR= "idImageUri";
        public static final String COLUMN_NAME_TAG= "itemTag";

    }


    public static abstract class Images implements BaseColumns {

        public static final String TABLE_NAME = "Images";

        public static final String COLUMN_ID_IMAGE = "idItemImage";

        public static final String COLUMN_NAME_IMAGE = "nameItemImage";
        public static final String COLUMN_NAME_TITLE = "titleItemImage";

    }



}
