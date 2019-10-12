package com.zippyttech.alist.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.zippyttech.alist.model.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zippyttech on 27/10/17.
 */

public class Utils {

    private static final String ERROR_TAG = "ERROR";
    public static final String SHARED_PREFERENCES_KEY = "shared_key";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public static Typeface centuryType(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/century_gothic.ttf");
    }

    public static boolean isExternalStorage(){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState() )
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()) ){
                Log.i("ExternalStorage","Si, es Leible");
            return true;
        }else{
            return false;
        }

    }

    public static boolean checkPermission(Context context, String permission){
        int check = ContextCompat.checkSelfPermission(context,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public static String JsonCustomerImage(String b64Image){
        String OPEN="{",CLOCED="}";
        String COM="\"";

        return OPEN + COM + "image" + COM + ":" + COM +  b64Image + COM + CLOCED;

    }

    public static boolean isExistFile(Context context, String nameFile){
        File file = new File(Environment.getExternalStorageDirectory(), "AnimeListDB");

        return file.exists();
    }

    public static void createFileAnimeListDB(Context context, String text,String name,String extend ){
        try {

            File nuevaCarpeta = new File(Environment.getExternalStorageDirectory(), "AnimeListDB");
            if (!nuevaCarpeta.exists()) {
                nuevaCarpeta.mkdir();
            }

            String FULLNAME = name + "."+extend;
            try {
                File file = new File(nuevaCarpeta, FULLNAME);
                file.createNewFile();

                OutputStreamWriter fout1 = null;
                fout1 = new OutputStreamWriter(new FileOutputStream(file));
                fout1.write(text);
                fout1.close();
                Toast.makeText(context, "Datos guardados.", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e){
                e.printStackTrace();
            } catch (Exception ex) {
                Log.e("Error", "ex: " + ex);
            }
        } catch (Exception e) {
            Log.e("Error", "e: " + e);
        }

    }

    public static List<VideoModel> loadDataDBFiles(Context context) throws FileNotFoundException, IOException, JSONException {
        Log.i("loatData","leyendo...");
        settings = context.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        editor = settings.edit();
        List<VideoModel> list = new ArrayList<>();
        String cadena;
        StringBuilder content = new StringBuilder();
        content.append("");
        String nameFile = settings.getString("data", "data") + ".json";
        FileReader f = new FileReader(Environment.getExternalStorageDirectory()+"/AnimeListDB/"+nameFile);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {
           content.append(cadena);
            System.out.println(cadena);
        }
        b.close();
        String resp = content.toString();
        Log.w("loadDataDBFiles",resp);
        JSONArray array = new JSONArray(resp);
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            VideoModel a = new VideoModel();
            int id = item.getInt("Id");
            String name = item.getString("Nombre");
            String image = item.getString("Imagen");
            int cap = item.getInt("Capitulo");
            String stat = item.getString("Estado");
            String day = item.getString("Proximo_Episodio");
            String color = item.getString("Color");
            String dateC = item.getString("Fecha_de_Creación");
            String dateU = item.getString("Fecha_Actualizada");
          a.setId(id);
          a.setTitle(name);
          a.setImage64(image);
          a.setCap(cap);
          a.setmStat(stat);
          a.setmDay(day);
          a.setmColor(color);
          a.setmDateC(dateC);
          a.setmDateU(dateU);
          list.add(a);
                Log.w("loadDataDBFiles","name: "+a.getTitle());
        }



        return list;
    }

    public static List<VideoModel> loadDataImageDBFiles() throws FileNotFoundException, IOException, JSONException {
        Log.i("loatData","leyendo...");
        List<VideoModel> list = new ArrayList<>();
        String cadena;
        StringBuilder content = new StringBuilder();
        content.append("");
        FileReader f = new FileReader(Environment.getExternalStorageDirectory()+"/AnimeListDB/images.json");
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {
            content.append(cadena);
            System.out.println(cadena);
        }
        b.close();
        String resp = content.toString();
        Log.w("loadDataImageDBFiles",resp);
        JSONArray array = new JSONArray(resp);
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            VideoModel a = new VideoModel();
            int id = item.getInt("Id");
            String name = item.getString("Nombre");
            String image = item.getString("Imagen");
            a.setId(id);
            a.setTitle(name);
            a.setImage64(image);
            list.add(a);
            Log.w("loadDataDBFiles","name: "+a.getTitle());
        }
        return list;
    }

    public static String saveDataImageFiles(List<VideoModel> list) {
        StringBuilder lista = new StringBuilder();
        Iterator<VideoModel> iterator = list.iterator();
        for (VideoModel a: list){
            try {
                JSONObject jsIma = new JSONObject();

                jsIma.put("Id",a.getId());
                jsIma.put("Nombre",a.getTitle());
                jsIma.put("Imagen",a.getImage64());

                iterator.next();
                if(iterator.hasNext()){
                    lista.append(jsIma.toString()).append(",");
                }else {
                    lista.append(jsIma.toString()).append("\n");
                }

                lista.append(jsIma.toString());

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return "["+lista.toString()+"]";
    }

    public static String saveDataFiles(List<VideoModel> list) {
        StringBuilder lista = new StringBuilder();
        Iterator<VideoModel> iterator = list.iterator();
        for (VideoModel a: list){
            try {
                JSONObject js = new JSONObject();
                JSONObject jsIma = new JSONObject();

                js.put("Id",a.getId());
                jsIma.put("Id",a.getId());
                jsIma.put("Nombre",a.getTitle());
                jsIma.put("Imagen",a.getImage64());
                js.put("Imagen",a.getImage64());
                js.put("Nombre",a.getTitle());
                js.put("Capitulo", a.getCap());
                js.put("Estado", a.getStat());
                js.put("Proximo_Episodio",a.getmDay());
                js.put("Color",a.getmColor());
                js.put("Fecha_de_Creación",a.getDateC());
                js.put("Fecha_Actualizada",a.getDateU());

//                js.put("Fecha_de_Creación",UtilsDate.reformateDate(a.getDateCreated(),setup.date.FORMAT,setup.date.FORMAT_SIMPLE2));
//                js.put("Fecha_Actualizada", UtilsDate.reformateDate(a.getDateUpdate(),setup.date.FORMAT,setup.date.FORMAT_SIMPLE2));

                iterator.next();
                if(iterator.hasNext()){
                    lista.append(js.toString()).append(",");
                }else {
                    lista.append(js.toString()).append("\n");
                }


            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return "["+lista.toString().replaceAll(",",",\n")+"]";
    }

    public static int isStat(String status) {
        int value=0;
        switch (status){
            case "En emisión":
                value = 1;
                break;
            case "Finalizado":
                value = 4;
                break;
            case "Estreno":
                value = 3;
                break;
            case "Olvidado":
                value = 2;
                break;
            default:
                value = 0;
                break;
        }
        return value;
    }

//    public static void newNotificacion(Context context, int cont, String title, String contentText, String ticker,int icon){
//
//        Intent intent = new Intent(context, SyncNotification.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//                new Intent(context, NavigationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
////        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        // Build notification
//        // Actions are just fake
//        Notification noti = new Notification.Builder(context)
//                .setContentTitle(title)
//                .setContentText(contentText)
//                .setSmallIcon(icon)
//                .setContentIntent(contentIntent)
//                .setTicker(ticker)
//                .setContentInfo(""+cont)
//                .setSound(sonido)
//                .build();
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        // hide the notification after its selected
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(cont, noti);
//    }


    public static void showFile(Context context, Uri fileUri){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "file/*");
        context.startActivity(intent);
    }

    public static String StringJoiner(String delimiter, String... args) throws NullPointerException {
        String response = "";
        int i = 0;
        for (String value :
                args) {

            if (args.length > 1)
                response = i <= args.length - 2 ? response + value + delimiter : response + value;

            i++;
        }
        return response;
    }

    public static String completeList(int position, String name){
        return ""+position+".- "+name;
    }

    public  static  boolean isMatchRegex(String regex, String exp){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(exp);
        return m.matches();
    }

    /**
     * Encode image Bitmap to Base64
     *
     * @param bm
     * @return
     */
    public static String encodeImage(Bitmap bm) {
        Log.w("encodeImage","image bounds: "+ bm.getWidth()+", "+bm.getHeight());

        if (bm.getHeight() <= 400 && bm.getWidth() <= 400) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            return (Base64.encodeToString(b, Base64.DEFAULT)).replaceAll("\\n", "");
        }
        int mHeight=400;
        int mWidth=400;

        if(bm.getHeight()>bm.getWidth()){
          float div=(float)bm.getWidth()/((float) bm.getHeight());
           float auxW=div*480;
            mHeight=480;
            mWidth=Math.round(auxW);
            Log.w("encodeImage","new high: "+mHeight+" width: "+mWidth);
        }
        else{
            float div= ((float) bm.getHeight())/(float)bm.getWidth();
            float auxH=div*480;
            mWidth=480;
            mHeight=360;
          mHeight=Math.round(auxH);
            Log.w("encodeImage","new high: "+mHeight+" width: "+mWidth);
        }

        bm = Bitmap.createScaledBitmap(bm, mWidth, mHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return (Base64.encodeToString(b, Base64.DEFAULT)).replaceAll("\\n", "");
    }

    public static String dateFormatAll(String formate) {
        //// TODO: formato de fecha dd-MM-yyyy HH:mm:ss      31-12-2017 17:59:59
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(formate);
        return formatter.format(date);
    }

    public static String dateFormat(String data, String formate) {
        //// TODO: formato de fecha dd-MM-yyyy HH:mm:ss      31-12-2017 17:59:59
        Date date = StringToDate(data, formate);
        SimpleDateFormat formatter = new SimpleDateFormat(formate);
        return formatter.format(date);
    }

    public static Integer Epoch(String timestamp,String formate){
        if(timestamp == null) return null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(formate);
            Date dt = sdf.parse(timestamp);
            long epoch = dt.getTime();
            return (int)(epoch/1000);
        } catch(ParseException e) {
            return null;
        }
    }


    public static String reformateDate( String Date, String formate, String formate2) {
//        dateOld =>  YYYY-MM-DD
        Date date = null;
        String resp = null;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseador = new SimpleDateFormat(formate);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formateador = new SimpleDateFormat(formate2);

        try {
            date = parseador.parse(Date);
            resp = formateador.format(date);
            return resp;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String DateToString(Date date, String formate){
        String string="";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(formate);
        string = sdf.format(new Date());

        return string;
    }

    public static Date StringToDate(String string, String formate){
        DateFormat format = new SimpleDateFormat(formate, Locale.ENGLISH);
        try {
           return format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DateVariable(String fecha, int dias, String formate) {//TODO: Suma y/o Resta a FECHAS
        //tomo string fecha sumo o resto "dias" y devuelvo el string modificado de fecha
        Date date=null;
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat(formate);
        try {
            date = format.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        Date dcalenda = calendar.getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(formate);
        String s = sdf.format(dcalenda);
        return s;
    }

    public static String filterTextView( String Date, String formate, String formate2) {
//        dateOld =>  YYYY-MM-DD
        Date date = null;
        String resp = null;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseador = new SimpleDateFormat(formate);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formateador = new SimpleDateFormat(formate2);

        try {
            date = parseador.parse(Date);
            resp = formateador.format(date);
            return resp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resp;
    }
    public static String filterTextViewMonth( String Date, String formate, String formate2) {
//        dateOld =>  YYYY-MM-DD
        Date date = null;
        String resp = null;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseador = new SimpleDateFormat(formate);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formateador = new SimpleDateFormat(formate2);

        try {
            date = parseador.parse(Date);
            resp = formateador.format(date);
            return resp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public static String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public  static  double parceDecimal(double value){
        //TODO: limata el numero de decimales por redondeo 123.456789 -> 123.46
        double resp=0;
        DecimalFormat df = new DecimalFormat("#.00");
       resp = Double.parseDouble(df.format(value));
       return resp;
    }

    public static double shortDecimal(double date){
        String format="#.#####";
        String resp="0.0";
        DecimalFormat df;
        double rep=0.0;
                df = new DecimalFormat("#.#####");
                resp =  df.format (date);
                rep = Double.parseDouble(resp);
         return rep;
    }

    public static double decimal (double date, int decimal){
        String format="#.#####";
        String resp="0.0";
        DecimalFormat df;

        switch(decimal){
            case -1:
                String str = String.valueOf(date);
                str = str.substring( str.indexOf('.') );
                resp = str.substring(1);
                break;
            case 0:
                df = new DecimalFormat("#");
                resp =  df.format (date);
                break;
            case 1:
                df = new DecimalFormat("#.#");
                resp =  df.format (date);
                break;
            case 2:
                df = new DecimalFormat("#.##");
                resp =  df.format (date);
                break;
            case 3:
                df = new DecimalFormat("#.###");
                resp =  df.format (date);
                break;
            case 4:
                df = new DecimalFormat("#.####");
                resp =  df.format (date);
                break;
            case 5:
                df = new DecimalFormat("#.#####");
                resp =  df.format (date);
                break;
            default:
                break;

        }
        return Double.parseDouble(resp);
    }

    public static String getTimeZone() {
        Calendar cal = Calendar.getInstance();
        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
// Got local offset, now loop through available timezone id(s).
        String[] ids = TimeZone.getAvailableIDs();
        String name = null;
        for (String id : ids) {
            TimeZone tz = TimeZone.getTimeZone(id);
            int hours = tz.getDSTSavings();
            int raw = tz.getRawOffset();
            int s = tz.getOffset(milliDiff);
            if (tz.getRawOffset() == milliDiff) {
                // Found a match.
                name = id;
                break;
            }
        }

        return name;
    }

    public static float getHoursTimeZone() {
        Calendar calendar = Calendar.getInstance();
        long milliDiff = calendar.get(Calendar.ZONE_OFFSET);
        float hours = (milliDiff / 1000) / 3600;
        return hours;
    }

    public static String dateTodayGMTFormat(String value_format) {
        Date date = Calendar.getInstance().getTime();
        String timezone = getTimeZone();
        SimpleDateFormat formatter = new SimpleDateFormat(value_format);
        String today = formatter.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // Configuramos la fecha que se recibe
        int hours_add = (int) (getHoursTimeZone() * (-1));
        calendar.add(Calendar.HOUR, hours_add);  // numero de horas a añadir, o restar en caso de horas<0
        calendar.add(Calendar.MINUTE, -5); // revisar 5 minutos antes de la hora del server
        Date newDate = calendar.getTime();
        today = formatter.format(newDate);
        return today;
    }

    public static String dateTodayFormat(String valueFormat) {
        Date date = Calendar.getInstance().getTime();
        String timezone = getTimeZone();
        SimpleDateFormat formatter = new SimpleDateFormat(valueFormat);
        String today = formatter.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // Configuramos la fecha que se recibe
        int hours_add = (int) (getHoursTimeZone() * (-1));
        //calendar.add(Calendar.HOUR, hours_add);  // numero de horas a añadir, o restar en caso de horas<0
        // calendar.add(Calendar.MINUTE,-30); // revisar 5 minutos antes de la hora del server
        Date newDate = calendar.getTime();
        today = formatter.format(newDate);
        return today;
    }

    /**
     * Check if a String have json format
     * @return
     */

    public static boolean isJSONFormat(String str){
        try{
            JSONObject obj = new JSONObject(str);
            int n=obj.names().length();
            if(n>=1) return true;
            else return false;

        }
        catch (JSONException js){
            Log.w("Utils","no json format");
            return false;
        }
    }

    /**
     * Is Network Conection availaible (WiFi or Network)
     *
     * @return
     */
    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * ARRAY OF ERROR TRACE ELEMENTS
     * @param e
     * @return
     */
    public static String ListStackTrace(Exception e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();

    }

    public static int differencesDate(String dateVisite, String dateCompare) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date fechaInicial= null;
        Date fechaFinal= null;
        try {
            fechaInicial = dateFormat.parse(dateVisite);
            fechaFinal=dateFormat.parse(dateCompare);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int dias=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/86400000);

        if(dias<0)return dias *= (-1);
        else return dias;
    }

    public static String changeData(String dateOld){
//     dateOld =>  YYYY-MM-DD
        Date date = null;
        String resp = null;

        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

        try {
            date = parseador.parse(dateOld);
            resp = formateador.format(date);
            return resp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resp;
    }

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

}
