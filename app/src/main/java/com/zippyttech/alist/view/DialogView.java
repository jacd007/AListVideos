package com.zippyttech.alist.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zippyttech.alist.common.UtilsImage;
import com.zippyttech.alist.model.VideoModel;
import com.zippyttech.alist.R;

/**
 * Created by zippyttech on 18/10/18.
 */

public abstract class DialogView {
    private static final String TAG="DialogViewx";
    private static Dialog dialog;
    private static Context c;
    private static final String SHARED_KEY = "shared_key";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private static ImageView ivImage,btnExit;
    private static VideoModel vm;
    private static TextView tvName,tvCap,tvStat,tvDay,tvDate;


    public static void preView(Context context, VideoModel list){
        c = context;
        dialog = new Dialog(context, R.style.Theme_AppCompat_DayNight_Dialog);
//        dialog = new Dialog(context,R.style.Animate3);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_details);

        btnExit    = (ImageView)   dialog.findViewById(R.id.iv_dialog_cancel);
        ivImage = (ImageView) dialog.findViewById(R.id.iv_dialog_image);
        tvName = (TextView) dialog.findViewById(R.id.tv_dialog_name);
        tvCap = (TextView) dialog.findViewById(R.id.tv_dialog_cap);
        tvStat = (TextView) dialog.findViewById(R.id.tv_dialog_stat);
        tvDay = (TextView) dialog.findViewById(R.id.tv_dialog_day);
        tvDate = (TextView) dialog.findViewById(R.id.tv_dialog_date);

        vm = new VideoModel();
        vm = list;

        setDataItem(vm);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void preViewImage(Context context, VideoModel list, boolean isCancelable){
        c = context;
        dialog = new Dialog(context, R.style.Theme_AppCompat_DayNight_Dialog);
//        dialog = new Dialog(context,R.style.Animate3);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.dialog_image);

        btnExit    = (ImageView)   dialog.findViewById(R.id.iv_dialog_cancel);
        ivImage = (ImageView) dialog.findViewById(R.id.iv_dialog_image);
        tvName = (TextView) dialog.findViewById(R.id.tv_dialog_name);

        vm = new VideoModel();
        vm = list;

        tvName.setText(""+ vm.getTitle());
        setImage(c, vm.getImage64(),ivImage);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void setDataItem(VideoModel aa) {
        tvName.setText(aa.getTitle());
        tvCap.setText("Cap: "+aa.getCap());
        tvStat.setText(aa.getStat());
        tvDay.setText(aa.getmDay());
        tvDate.setText(aa.getDateU());

        setImage(c,aa.getImage64(),ivImage);
    }

    private static void setImage(Context context, String mImage, ImageView imageuser){
        try {
            if (!mImage.equals("null")) {
                if (!mImage.substring(0, 4).equals("http")) {
                    if (!mImage.substring(0, 23).equals("data:image/jpeg;base64,")) {
                        mImage = "data:image/jpeg;base64," + mImage;
                    }
                    Bitmap bitmap = UtilsImage.b64ToBitmap(mImage);
                    imageuser.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 1250, 1250, false));
                } else {
                    Glide.with(context)
                            .load(mImage)
                            .placeholder(R.drawable.ic_broken_image)
                            .error(R.drawable.ic_no_image)
                            .into(imageuser);
                }
            }else {
                Glide.with(context)
                        .load("xxxx")
                        .error(R.drawable.ic_no_image)
                        .into(imageuser);
            }
        }catch (VerifyError e){
            imageuser.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_no_image));
            e.printStackTrace();
            Log.e("setmColor",e.getMessage());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            Glide.with(context)
                    .load(mImage)
                    .placeholder(R.drawable.ic_broken_image)
                    .error(R.drawable.ic_no_image)
                    .into(imageuser);
            Log.e("setmColor",e.getMessage());
        }catch (NullPointerException e){
            Log.e("setmColor",e.getMessage());
        }
    }



}
