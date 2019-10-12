package com.zippyttech.alist.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zippyttech.alist.R;
import com.zippyttech.alist.model.VideoModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class UtilsGson {
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static List<VideoModel> list;
    private static List<VideoModel> listx;

    public static String ListToString(List<VideoModel> listAccount){
        Gson gson = new Gson();
        return gson.toJson(listAccount);
    }

    public static List<VideoModel> StringToList(String JSON_STRING){
       listx = new ArrayList<>();
        list = new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<VideoModel>>(){}.getType();
        list = gson.fromJson(JSON_STRING, type);
        list = (list == null) ? listx : list;
        return list;
    }

    public static void saveList(Context context, List<VideoModel> listAccount){
        Gson gson = new Gson();
        String stringJSON = gson.toJson(listAccount);

        settings = context.getSharedPreferences(context.getResources().getString(R.string.SHARED_KEY), 0);
        editor = settings.edit();
        editor.putString(UtilsItemList.myLIST,stringJSON);
        editor.commit();
        Toast.makeText(context, "Guardado.", Toast.LENGTH_SHORT).show();
    }

    public static List<VideoModel> loadList(Context context){
        list = new ArrayList<>();
        Gson gson = new Gson();
        String stringJSON = gson.toJson(list);
        settings = context.getSharedPreferences(context.getResources().getString(R.string.SHARED_KEY), 0);
        editor = settings.edit();
        String aux = settings.getString(UtilsItemList.myLIST,stringJSON);
        list = StringToList(aux);
        Toast.makeText(context, "Cargado.", Toast.LENGTH_SHORT).show();
        return list;
    }

}
