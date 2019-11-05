package com.zippyttech.alist.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.zippyttech.alist.common.Utils;
import com.zippyttech.alist.common.UtilsItemList;
import com.zippyttech.alist.common.UtilsGson;
import com.zippyttech.alist.data.AListDB;
import com.zippyttech.alist.view.EditActivity;
import com.zippyttech.alist.R;
import com.zippyttech.alist.model.VideoModel;
import com.zippyttech.alist.view.DialogView;
import com.zippyttech.alist.view.SelectListActivity;
import com.zippyttech.datelib.DateUtils.UtilsDate;

import java.util.ArrayList;
import java.util.List;

class ListViewHolder extends RecyclerView.ViewHolder {

    ImageButton mSetting;
    ImageView mIcon;
    TextView mTitle,mCap,mNameCapType;
    View mVColor;


    ListViewHolder(View itemView, AppCompatActivity activity) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.tv_item_name);
        mCap = (TextView) itemView.findViewById(R.id.tv_item_cap);
        mNameCapType = (TextView) itemView.findViewById(R.id.tv_item_name_cap);
        mIcon = (ImageView) itemView.findViewById(R.id.ibtn_item_details);
        mVColor = (View) itemView.findViewById(R.id.v_item_color);
        mSetting = (ImageButton) itemView.findViewById(R.id.ibtn_item_setting);

    }
}
public class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
    private static final String TAG = "ListAdapter";

    private SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    List<VideoModel> mData = new ArrayList<>();
    private AppCompatActivity Activity;
    private Context context;
    TextView tvCount;

    public ListAdapter(List<VideoModel> mData, Context context, AppCompatActivity acti,TextView textView) {
        this.mData = mData;
        this.context = context;
        this.Activity = acti;
        this.tvCount = textView;
    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item, parent, false);
        return new ListViewHolder(itemView,Activity);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        settings = context.getSharedPreferences(context.getResources().getString(R.string.SHARED_KEY), 0);
        editor = settings.edit();

            holder.mTitle.setText(""+mData.get(position).getTitle());
            holder.mCap.setText(""+mData.get(position).getCap());
            holder.mNameCapType.setText(mData.get(position).getmOther());
            holder.mVColor.setBackgroundColor(Color.parseColor(mData.get(position).getmColor()));

            String DAY = UtilsDate.dateTodayFormat("EEEE").toLowerCase();
//            DAY = UtilsDate.reformateDate("2019-10-13 12:00:00","yyyy-MM-dd HH:mm:ss","EEEE").toLowerCase();
//        Log.w("onBindViewHodler","DAY: "+DAY+" DATE: "+mData.get(position).getmDay().toLowerCase());

            if (mData.get(position).getmDay().toLowerCase().equals(DAY)){
                holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_notifications_active));
            }
//            else if(mData.get(position).getmDay().equals("No Asignado")){
////                setHolderImage(mData.get(position).getImage64(),holder.mIcon);
//                holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_report_problem));
//            }
            else {
                switch (mData.get(position).getmStat()) {
                    case "No Asignado":
                        holder.mCap.setVisibility(View.VISIBLE);
                        holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_report_problem));
//                        setHolderImage(mData.get(position).getImage64(),holder.mIcon);
                        break;
                    case "En emisión":
                        holder.mCap.setVisibility(View.VISIBLE);
                        holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_schedule));
                        break;
                    case "Finalizado":
                        holder.mCap.setVisibility(View.GONE);
                        holder.mNameCapType.setText("Fin");
                        holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_check_circle));
                        break;
                    case "Estreno":
                        holder.mCap.setVisibility(View.INVISIBLE);
                        holder.mCap.setEnabled(false);
                        holder.mVColor.setBackgroundColor(Color.YELLOW);
                        setHolderImage(mData.get(position).getImage64(),holder.mIcon);
                        break;
                    case "Olvidado":
                        holder.mCap.setVisibility(View.VISIBLE);
                        holder.mVColor.setBackgroundColor(Color.WHITE);
                        holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_schedule_undefined));
                        break;
                }

            }

            holder.mSetting.setOnClickListener((v)->{

                    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                    builder.setItems(new CharSequence[]{"Editar", "Borrar Item"}, (dialogInterface, i) -> {
                        switch (i){
                            case 0:
                                try {
                                    List<VideoModel> AuxList = new ArrayList<>();
                                    AuxList.add(mData.get(position));
                                    editor.putString("AUX_IMAGE",mData.get(position).getImage64());
                                    editor.commit();
                                Intent intent = new Intent(context, EditActivity.class);
                                intent.putExtra(UtilsItemList.myLIST, UtilsGson.ListToString(AuxList));
                                    intent.putExtra("new",false);
                                intent.putExtra("position",position);
                                    ((SelectListActivity) context).startActivityForResult(intent,1500);
                                }catch (Exception e){e.printStackTrace();}
                                break;
                            case 1:
                                alertDialogWarning(position,mData.get(position));
                                break;
                        }
                    });

                    builder.setCancelable(true);
                    builder.setTitle(""+mData.get(position).getTitle());
                    builder.show();

            });

            holder.mIcon.setOnClickListener((v)->{
                try {
                    DialogView.preViewImage(context,mData.get(position),true);
                }catch (InflateException e){
                    e.printStackTrace();
                    Toast.makeText(context, "Imagen de "+mData.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }

            });

            holder.mTitle.setOnClickListener((v)->{
                Toast.makeText(context, "Estado: "+mData.get(position).getmStat(), Toast.LENGTH_SHORT).show();
            });

            holder.mCap.setOnClickListener((v)->{
                Toast.makeText(context, "Día: "+mData.get(position).getmDay(), Toast.LENGTH_SHORT).show();
            });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VideoModel sm = mData.get(position);
//                Toast.makeText(context, "Hi "+sm.getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void setHolderImage(String mImage, ImageView mImageView) {
        try {
            if(!mImage.equals("")) {
                if (!mImage.contains("http")) {
                    if (!mImage.contains(" ")){
                        Bitmap bitmap = Utils.Base64ToBitmap(mImage);
                        bitmap = Utils.resizeImage(context,bitmap,40,40);
                        mImageView.setImageBitmap(bitmap);
                    }else {
                        mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_no_image));
                    }

                } else {
                    Glide.with(context)
                            .load(mImage)
                            .placeholder(R.drawable.ic_broken_image)
                            .error(R.drawable.ic_no_image)
                            .into(mImageView);
                }
            }
            else {
                Glide.with(context)
                        .load(mImage)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_no_image)
                        .into(mImageView);
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_no_image));
        }catch (VerifyError e) {
            mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_no_image));
            e.printStackTrace();
        }
    }

    public List<VideoModel> getList(){
        return mData;
    }

    public void refresh(List<VideoModel> list){
        this.mData = list;
        this.notifyDataSetChanged();
    }

    public void refresh(){
        this.notifyDataSetChanged();
        tvCount.setText(""+getItemCount());
    }

    public void setNewItem(@NonNull List<VideoModel> mItemList){
        mData.addAll(mItemList);
        this.notifyDataSetChanged();
        tvCount.setText(""+getItemCount());
    }

    public void replaceItem(List<VideoModel> mItemList, int position){
        mData.set(position,mItemList.get(0));
        this.notifyDataSetChanged();
        tvCount.setText(""+getItemCount());
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else
            return 0;
    }

    private void alertDialogWarning(final int position, final VideoModel vm){
        final boolean[] x = {false};
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Eliminar Item Seleccionado");
        alertDialog.setCancelable(false);

        alertDialog.setMessage("¿esta seguro que desea eliminar, \""+ vm.getTitle() +"\"?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Eliminando "+vm.getTitle()+"...", Toast.LENGTH_SHORT).show();
                        mData.remove(position);
                        AListDB aListDB = new AListDB(context);
                        aListDB.deleteItemProductByTag(vm.getTag());
                        refresh();
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }



}



















