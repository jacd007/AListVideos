package com.zippyttech.alist.common;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.zippyttech.alist.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by zippyttech on 27/10/17.
 */

public class UtilsImage {

    private static final String OK_TAG = "UtilsImage";
    private static final String ERROR_TAG = "ERROR";
    private SharedPreferences settings;
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


    public static void canvasToFile(Context context, Canvas canvas, FileOutputStream fos, Bitmap bmpBase,
                                    int image_width, int image_height, String name){
        File filesDir = new File(Environment.getExternalStorageDirectory(), "Data/"+context.getResources().getString(R.string.app_name));
        if (!filesDir.exists()) {
            filesDir.mkdir();
        }
        File imageFile = new File(filesDir, name + ".png");

        bmpBase = Bitmap.createBitmap(image_width, image_height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bmpBase);
// draw what ever you want canvas.draw...

// Save Bitmap to File
        try
        {
            fos = new FileOutputStream(imageFile);
            bmpBase.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            fos = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                    fos = null;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void convertBitmapToFile(Context context, Bitmap bitmap, String name) {
//        File filesDir = context.getFilesDir();
        File filesDir = new File(Environment.getExternalStorageDirectory(), "AnimeListDB/Images");
        if (!filesDir.exists()) {
            filesDir.mkdir();
        }
        File imageFile = new File(filesDir, name + ".png");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            Toast.makeText(context, "Creacion de Imagen con exito!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("convertBitmapToFile", "Error writing bitmap", e);
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap Base64ToBitmap(String encodedImagebase64) {
      //  String pureBase64Encoded=p;
        if (encodedImagebase64 != null) {
          try {
         String pureBase64Encoded = encodedImagebase64.substring(encodedImagebase64.indexOf(",") + 1);
              byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
              Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
              return decodedByte;
          }
          catch (Exception e){
              Log.e("ERROR UTILS","base64image "+e.getLocalizedMessage());
              e.printStackTrace();

          }

        }

        return null;
    }

    public static Bitmap b64ToBitmap(String base64String){
        try {
            String base64Image = base64String.split(",")[1];
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodeByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            return decodeByte;
        }catch (Exception e){
            return null;
        }
    }
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
            mWidth= Math.round(auxW);
            Log.w("encodeImage","new high: "+mHeight+" width: "+mWidth);
        }
        else{
            float div= ((float) bm.getHeight())/(float)bm.getWidth();
            float auxH=div*480;
            mWidth=480;
            mHeight=360;
          mHeight= Math.round(auxH);
            Log.w("encodeImage","new high: "+mHeight+" width: "+mWidth);
        }

        bm = Bitmap.createScaledBitmap(bm, mWidth, mHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return (Base64.encodeToString(b, Base64.DEFAULT)).replaceAll("\\n", "");
    }

    public static Bitmap getDateFromFile(String address, String name){
        try {
            File sdCard = Environment.getExternalStorageDirectory();

            File directory = new File(sdCard.getAbsolutePath() + address);

            File file = new File(directory, name+".jpg"); //or any other format supported

            FileInputStream streamIn = null;

                streamIn = new FileInputStream(file);

            Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image

                streamIn.close();

                return bitmap;
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeFile(File f, int WIDTH, int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


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

    private String getFileExtension(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 70, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static String uriToBase64(Context context, Uri path) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 70, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
