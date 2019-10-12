package com.zippyttech.alist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.zippyttech.alist.common.Utils;
import com.zippyttech.alist.model.ImageModel;
import com.zippyttech.alist.model.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zippyttech on 21/08/17.
 */

public class AListDB {

    private static SQLiteDatabase db;
    private static AListDBHelper mDbHelper;
    private Context context;
    private static final String TAG = "AListDB";

    public AListDB(Context context) {

        mDbHelper = new AListDBHelper(context);
        this.context = context;
    }

    /**
     * @param tableName  name of table of dataBase to select registers
     * @param projection that specifies which columns from the table of database
     * @param where      Filter results WHERE "qr" = 'My qr'
     * @param args       arguments to where = {"My Caegory"}
     * @return
     */

    public Cursor selectRows(String tableName, String[] projection, String where, String[] args) {

        db = mDbHelper.getReadableDatabase();
/*
        Cursor c = db.query(
                tableName,                                  // The table to query
                projection,                                 // The columns to return
                where,                                      // The columns for the WHERE clause
                args,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortOrder                                   // The sort order
        );
*/
        String proj = Utils.StringJoiner(", ", projection);
        String argWhere = "";

        if (args.length > 1)
            argWhere = Utils.StringJoiner(", " + args);
        else if (args.length == 1)
            argWhere = args[0];

        String query = "SELECT " + proj + " FROM " + tableName + " " + where + " " + argWhere;

        Cursor c = db.rawQuery(query, null);

        return c;

    }

    /**
     * where and args separated by comma
     *
     * @param tableName
     * @param projection
     * @param where
     * @param args
     * @return
     */

    public Cursor selectRows(String tableName, String projection, String where, String args) {

        db = mDbHelper.getReadableDatabase();
        String query = "SELECT " + projection + " FROM " + tableName + " " + where + " " + args + ";";
        Cursor c = db.rawQuery(query, null);
        return c;

    }

    /**
     * @param tableName name of table of dataBase
     * @param values    values to new register
     */
    public long insertRow(String tableName, ContentValues values) {

        db = mDbHelper.getWritableDatabase();
        long insert = db.insert(tableName, null, values);
        db.close();
        Log.i("DB insert", tableName + ": " + String.valueOf(insert));
        return insert;

    }

    /**
     * Update Tables: where format => id= "01"
     * @param tableName
     * @param value
     * @param where
     * @return
     */

    public long updateRow(String tableName, ContentValues value, String where){

        db = mDbHelper.getWritableDatabase();
        long update= db.update(tableName, value, where, null);
        db.close();
        Log.i("DB update", tableName + ": " + String.valueOf(update));
        return update;
    }

    /**
     * Select images by id item
     * @param iditem
     * @return
     */

    public List<ImageModel> GetImagesByIditem(String iditem) {
        try {
            List<ImageModel> imageGalleryList = new ArrayList<ImageModel>();

            String column_id = AListSchedule.Images._ID;
            String column_iditem = AListSchedule.Images.COLUMN_ID_IMAGE;
            String column_image = AListSchedule.Images.COLUMN_NAME_IMAGE;
            String column_title = AListSchedule.Images.COLUMN_NAME_TITLE;

            String argument = AListSchedule.Images.COLUMN_ID_IMAGE + "=" + iditem;
            Cursor c = selectRows(AListSchedule.Images.TABLE_NAME, "*", "WHERE", argument);

            if (c.moveToFirst()) {
                ImageModel imageGallery;
                do {
                    imageGallery = new ImageModel();
                    imageGallery.setId(c.getInt(c.getColumnIndex(column_id)));
                    imageGallery.setIdItem(c.getInt(c.getColumnIndex(column_iditem)));
                    imageGallery.setImage64(c.getString(c.getColumnIndex(column_image)));
                    imageGallery.setTitle(c.getString(c.getColumnIndex(column_title)));

                    imageGalleryList.add(imageGallery);

                } while (c.moveToNext());
            }
            return imageGalleryList;
        } catch (Exception e) {
            Log.e(TAG, "error obteniendo images por product");
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAll(){
        db = mDbHelper.getWritableDatabase();
        String deleteQuery="DELETE FROM "+ AListSchedule.Videos.TABLE_NAME;
        db.execSQL(deleteQuery);
        deleteQuery="DELETE FROM "+ AListSchedule.Images.TABLE_NAME;
        db.execSQL(deleteQuery);


    }


    /**
     * Insert images by id item
     * @param iditem
     * @param imageGalleriesList
     */

    public void SetImagesByIditem(String iditem, List<ImageModel> imageGalleriesList) {
        try {
            String table_name = AListSchedule.Images.TABLE_NAME;
            String column_id = AListSchedule.Images._ID;
            String column_iditem = AListSchedule.Images.COLUMN_ID_IMAGE;
            String column_image = AListSchedule.Images.COLUMN_NAME_IMAGE;
            String column_title = AListSchedule.Images.COLUMN_NAME_TITLE;


            for (ImageModel imagegallery :
                    imageGalleriesList) {

                ContentValues content = new ContentValues();
              //  content.put(column_id, imagegallery.getId());
                content.put(column_iditem, iditem);
                content.put(column_image, imagegallery.getImage64());
                content.put(column_title, imagegallery.getTitle());
                insertRow(table_name, content);

            }

        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros de images en db");
            e.printStackTrace();
        }


    }

        public void deleteItemProductById(String id){
            db = mDbHelper.getWritableDatabase();
//        String deleteQuery="DELETE FROM "+ ProductSchedule.Product.TABLE_NAME;
//        db.execSQL(deleteQuery);

            db.delete(AListSchedule.Videos.TABLE_NAME,
                    AListSchedule.Videos.COLUMN_NAME_ID + " = ?", new String[]{id});
            db.close();

        }


    public void eraseAllImagesById(String id){

        db = mDbHelper.getWritableDatabase();
    //    Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        String deleteQuery="DELETE FROM "+ AListSchedule.Images.TABLE_NAME+
                            " WHERE "+ AListSchedule.Images.COLUMN_ID_IMAGE +"="+id;
        db.execSQL(deleteQuery);



    }

    public VideoModel getitemById(String id){
        db = mDbHelper.getWritableDatabase();
        VideoModel item=null;
        String table_item= AListSchedule.Videos.TABLE_NAME;
        String column_id= AListSchedule.Videos.COLUMN_NAME_ID;


        Cursor c = db.rawQuery("SELECT * FROM " +table_item +" WHERE "+column_id+"="+id, null);
        c.moveToFirst();

        if (c.moveToFirst()) {

            do {
                item = new VideoModel();

                item.setId(c.getInt(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_ID)));
                item.setCap(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_CAPITULE))));
                item.setStat(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_STAT))));
                item.setDateC(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_DATA))));
                item.setDateU(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_DATA_UPDATE))));
                item.setType(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_TYPE))));
                item.setDay(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_DAY))));
                item.setColor(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_COLOR))));

                item.setTitle(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_TITLE)));
                item.setmDateC(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DATA)));
                item.setmDateU(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DATA_UPDATE)));
                item.setmStat(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_STATUS)));
                item.setmColor(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_COLOR)));
                item.setmDay(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DAY)));
                item.setmType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_TYPE)));
                item.setmOther(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_OTHERS)));

                item.setImage64(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_IMAGE)));
                item.setmType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_IMAGE_DIR)));
                item.setmType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_TAG)));

                Log.i(TAG, "getDB " + item.getTitle());

            } while (c.moveToNext());
        }

        return item;
    }

    public boolean isExistitem(String id){
        db = mDbHelper.getWritableDatabase();
        VideoModel item=null;
        String table_item= AListSchedule.Videos.TABLE_NAME;
        String column_id= AListSchedule.Videos.COLUMN_NAME_ID;


        Cursor c = db.rawQuery("SELECT * FROM " +table_item +" WHERE "+column_id+"="+id+";", null);
        c.moveToFirst();
        boolean exist=false;

        if (c.moveToFirst()) {
            exist=true;
            do {
               exist=true;

            } while (c.moveToNext());
        }

       return exist;
    }


    public void updateData(int id, List<VideoModel> list){

       try {
           if (isExistitem(""+id)){
               deleteItemProductById(""+id);
               setVideo(list);
           }else Log.e(TAG, "No Existe este anime");
       }catch (NullPointerException e){
           e.printStackTrace();
       }

    }

    public List<VideoModel> getVideo() {

        String dato = "";
        try {
            db = mDbHelper.getWritableDatabase();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        List<VideoModel> Product = new ArrayList<>();

       if(AListDBHelper.DATABASE_VERSION<1) {
           String[] projection = {
           AListSchedule.Videos.COLUMN_NAME_ID,
           AListSchedule.Videos.COLUMN_CAPITULE,
           AListSchedule.Videos.COLUMN_ID_STAT,
           AListSchedule.Videos.COLUMN_ID_DATA,
           AListSchedule.Videos.COLUMN_ID_DATA_UPDATE,
           AListSchedule.Videos.COLUMN_ID_TYPE,
           AListSchedule.Videos.COLUMN_ID_DAY,
           AListSchedule.Videos.COLUMN_ID_COLOR,

           AListSchedule.Videos.COLUMN_TITLE,
           AListSchedule.Videos.COLUMN_NAME_DATA,
           AListSchedule.Videos.COLUMN_NAME_DATA_UPDATE,
           AListSchedule.Videos.COLUMN_NAME_STATUS,
           AListSchedule.Videos.COLUMN_COLOR,
           AListSchedule.Videos.COLUMN_NAME_DAY,
           AListSchedule.Videos.COLUMN_NAME_TYPE,
           AListSchedule.Videos.COLUMN_NAME_OTHERS,

           AListSchedule.Videos.COLUMN_NAME_IMAGE,
           AListSchedule.Videos.COLUMN_NAME_IMAGE_DIR,
           AListSchedule.Videos.COLUMN_NAME_TAG
           };
       }

        //TODO: HACER UN "WHERE" PARA ENABLED

        String sortOrder = "";
        String arg[] = {"false"};
        Cursor c = db.rawQuery(
                "SELECT * FROM "
                        + AListSchedule.Videos.TABLE_NAME
                        +" ORDER BY  LOWER("+ AListSchedule.Videos.COLUMN_TITLE +")", null);
        c.moveToFirst();
        int count = c.getCount();

        if (c.moveToFirst()) {
            VideoModel item;
            do {
                item = new VideoModel();

                item.setId(c.getInt(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_ID)));
                item.setCap(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_CAPITULE))));
                item.setStat(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_STAT))));
                item.setDateC(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_DATA))));
                item.setDateU(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_DATA_UPDATE))));
                item.setType(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_TYPE))));
                item.setDay(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_DAY))));
                item.setColor(Integer.parseInt(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_ID_COLOR))));

                item.setTitle(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_TITLE)));
                item.setmDateC(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DATA)));
                item.setmDateU(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DATA_UPDATE)));
                item.setmStat(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_STATUS)));
                item.setmColor(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_COLOR)));
                item.setmDay(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DAY)));
                item.setmType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_TYPE)));
                item.setmOther(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_OTHERS)));

                item.setImage64(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_IMAGE)));
                item.setmType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_IMAGE_DIR)));
                item.setmType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_TAG)));

                Product.add(item);

            } while (c.moveToNext());
        }

        return Product;

    }

    public void setVideo(List<VideoModel> listVideos) {
        try {
            String table_name = AListSchedule.Videos.TABLE_NAME;

            String column_id = AListSchedule.Videos.COLUMN_NAME_ID;
            String column_cap = AListSchedule.Videos.COLUMN_CAPITULE;
            String column_i_stat = AListSchedule.Videos.COLUMN_ID_STAT;
            String column_i_data = AListSchedule.Videos.COLUMN_ID_DATA;
            String column_i_datau = AListSchedule.Videos.COLUMN_ID_DATA_UPDATE;
            String column_i_type = AListSchedule.Videos.COLUMN_ID_TYPE;
            String column_i_day = AListSchedule.Videos.COLUMN_ID_DAY;
            String column_i_color = AListSchedule.Videos.COLUMN_ID_COLOR;

            String column_title = AListSchedule.Videos.COLUMN_TITLE;
            String column_data = AListSchedule.Videos.COLUMN_NAME_DATA;
            String column_datau = AListSchedule.Videos.COLUMN_NAME_DATA_UPDATE;
            String column_stat = AListSchedule.Videos.COLUMN_NAME_STATUS;
            String column_color = AListSchedule.Videos.COLUMN_COLOR;
            String column_day = AListSchedule.Videos.COLUMN_NAME_DAY;
            String column_type = AListSchedule.Videos.COLUMN_NAME_TYPE;
            String column_others = AListSchedule.Videos.COLUMN_NAME_OTHERS;

            String column_image64 = AListSchedule.Videos.COLUMN_NAME_IMAGE;
            String column_image_dir = AListSchedule.Videos.COLUMN_NAME_IMAGE_DIR;
            String column_tag = AListSchedule.Videos.COLUMN_NAME_TAG;

            for (VideoModel vm
                    :
                    listVideos) {
                ContentValues register = new ContentValues();
                register.put(column_id, vm.getId());
                register.put(column_cap, vm.getCap());
                register.put(column_i_stat, vm.getStat());
                register.put(column_i_data, vm.getDateC());
                register.put(column_i_datau, vm.getDateU());
                register.put(column_i_type, vm.getType());
                register.put(column_i_day, vm.getDay());
                register.put(column_i_color, vm.getColor());

                register.put(column_title, vm.getTitle());
                register.put(column_data, vm.getmDateC());
                register.put(column_datau, vm.getmDateU());
                register.put(column_stat, vm.getmStat());
                register.put(column_color, vm.getmColor());
                register.put(column_day, vm.getmDay());
                register.put(column_type, vm.getmType());
                register.put(column_others, vm.getmOther());

                register.put(column_image64, vm.getImage64());
                register.put(column_image_dir, vm.getmImageDir());
                register.put(column_tag, vm.getTag());

                insertRow(table_name, register);
            }
        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }

    public void insertProduct(VideoModel vm) {
        try {
            String table_name = AListSchedule.Videos.TABLE_NAME;

            String column_id = AListSchedule.Videos.COLUMN_NAME_ID;
            String column_cap = AListSchedule.Videos.COLUMN_CAPITULE;
            String column_i_stat = AListSchedule.Videos.COLUMN_ID_STAT;
            String column_i_data = AListSchedule.Videos.COLUMN_ID_DATA;
            String column_i_datau = AListSchedule.Videos.COLUMN_ID_DATA_UPDATE;
            String column_i_type = AListSchedule.Videos.COLUMN_ID_TYPE;
            String column_i_day = AListSchedule.Videos.COLUMN_ID_DAY;
            String column_i_color = AListSchedule.Videos.COLUMN_ID_COLOR;

            String column_title = AListSchedule.Videos.COLUMN_TITLE;
            String column_data = AListSchedule.Videos.COLUMN_NAME_DATA;
            String column_datau = AListSchedule.Videos.COLUMN_NAME_DATA_UPDATE;
            String column_stat = AListSchedule.Videos.COLUMN_NAME_STATUS;
            String column_color = AListSchedule.Videos.COLUMN_COLOR;
            String column_day = AListSchedule.Videos.COLUMN_NAME_DAY;
            String column_type = AListSchedule.Videos.COLUMN_NAME_TYPE;
            String column_others = AListSchedule.Videos.COLUMN_NAME_OTHERS;

            String column_image64 = AListSchedule.Videos.COLUMN_NAME_IMAGE;
            String column_image_dir = AListSchedule.Videos.COLUMN_NAME_IMAGE_DIR;
            String column_tag = AListSchedule.Videos.COLUMN_NAME_TAG;

            ContentValues register = new ContentValues();
            register.put(column_id, vm.getId());
            register.put(column_cap, vm.getCap());
            register.put(column_i_stat, vm.getStat());
            register.put(column_i_data, vm.getDateC());
            register.put(column_i_datau, vm.getDateU());
            register.put(column_i_type, vm.getType());
            register.put(column_i_day, vm.getDay());
            register.put(column_i_color, vm.getColor());

            register.put(column_title, vm.getTitle());
            register.put(column_data, vm.getmDateC());
            register.put(column_datau, vm.getmDateU());
            register.put(column_stat, vm.getmStat());
            register.put(column_color, vm.getmColor());
            register.put(column_day, vm.getmDay());
            register.put(column_type, vm.getmType());
            register.put(column_others, vm.getmOther());

            register.put(column_image64, vm.getImage64());
            register.put(column_image_dir, vm.getmImageDir());
            register.put(column_tag, vm.getTag());

                insertRow(table_name, register);

        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }

    public int getSizeDB(){
        return getVideo().size();
    }

}
