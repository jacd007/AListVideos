package com.zippyttech.alist.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
           // holder.mIcon.setBackgroundColor(Color.parseColor(mData.get(position).getmColor()));

            String DAY = UtilsDate.dateTodayFormat("EEEE").toLowerCase();
//            DAY = UtilsDate.reformateDate("2019-10-13 12:00:00","yyyy-MM-dd HH:mm:ss","EEEE").toLowerCase();
//        Log.w("onBindViewHodler","DAY: "+DAY+" DATE: "+mData.get(position).getmDay().toLowerCase());

            if (mData.get(position).getmDay().toLowerCase().equals(DAY) &&mData.get(position).getStat()!=2){
                holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_notifications_active));
            }
            else {
                switch (mData.get(position).getmStat()) {
                    case "No Asignado":
                        holder.mCap.setVisibility(View.VISIBLE);
                        if (mData.get(position).getDay()!=0)
                            holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_mood_bad));
                        else

                            //setHolderImage(mData.get(position).getImage64(),holder.mIcon);
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
                        holder.mVColor.setBackgroundColor(Color.BLACK);
                        holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_schedule_undefined));
                        break;
                }

            }

            holder.mSetting.setOnClickListener((v)->{

                    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                    builder.setItems(new CharSequence[]{"Editar todo","Editar solo Capitulo/Episodio","Cambiar estado", "Borrar Este Video"}, (dialogInterface, i) -> {
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
                                intent.putExtra("color",mData.get(position).getColor());
                                    ((SelectListActivity) context).startActivityForResult(intent,1500);
                                }catch (Exception e){e.printStackTrace();}
                                break;
                            case 1:
                                preViewData(mData.get(position),position);
                                break;
                            case 2:
                                dialogEditStatus(mData.get(position),position);
                                break;
                            case 3:
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

    private int select=0;
    private void dialogEditStatus(VideoModel vm, int pos) {
        //#00FFFFFF
        final int stad = vm.getStat();
        Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_DayNight_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit_status);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_dialog_save);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel);
        ImageView ivImage = (ImageView) dialog.findViewById(R.id.iv_dialog_image);

        Button btnS0 = (Button) dialog.findViewById(R.id.btn_dialog_undefinited);
        Button btnS1 = (Button) dialog.findViewById(R.id.btn_dialog_schedule);
        Button btnS2 = (Button) dialog.findViewById(R.id.btn_dialog_finish);
        Button btnS3 = (Button) dialog.findViewById(R.id.btn_dialog_premiere);
        Button btnS4 = (Button) dialog.findViewById(R.id.btn_dialog_old);

        setHolderImage(vm.getImage64(),ivImage);

        select = vm.getStat();
        switch (vm.getStat()){
            case 0:
                btnS0.setBackground(context.getResources().getDrawable(R.drawable.border_selected_yelow));
                break;
            case 1:
                btnS1.setBackground(context.getResources().getDrawable(R.drawable.border_selected_yelow));
                break;
            case 2:
                btnS2.setBackground(context.getResources().getDrawable(R.drawable.border_selected_yelow));
                break;
            case 3:
                btnS3.setBackground(context.getResources().getDrawable(R.drawable.border_selected_yelow));
                break;
            case 4:
                btnS4.setBackground(context.getResources().getDrawable(R.drawable.border_selected_yelow));
                break;
        }

        btnSave.setOnClickListener((view -> {
            if (select!=stad){
                vm.setmDateU(UtilsDate.dateTodayFormat("yyyy-MM-dd HH:mm:ss"));
                vm.setDateU(UtilsDate.Epoch(vm.getmDateU(),"yyyy-MM-dd HH:mm:ss"));
                mData.set(pos,vm);
                refresh();

                AListDB aListDB = new AListDB(context);
                if (aListDB.getSizeDB()>0) aListDB.deleteAll();
                aListDB.setVideo(getList());
                Toast.makeText(context, "Item actualizado...", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Fallido causa: Mismo estado.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }));

        btnS0.setOnClickListener((view -> {
            select= 0;
            vm.setStat(select);
            btnS0.setBackground(context.getResources().getDrawable(R.drawable.border_selected));
            btnS1.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS2.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS3.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS4.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
        }));
        btnS1.setOnClickListener((view -> {
            select= 1;
            vm.setStat(select);
            btnS0.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS1.setBackground(context.getResources().getDrawable(R.drawable.border_selected));
            btnS2.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS3.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS4.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
        }));
        btnS2.setOnClickListener((view -> {
            select= 2;
            vm.setStat(select);
            btnS0.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS1.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS2.setBackground(context.getResources().getDrawable(R.drawable.border_selected));
            btnS3.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS4.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
        }));
        btnS3.setOnClickListener((view -> {
            select= 3;
            vm.setStat(select);
            btnS0.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS1.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS2.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS3.setBackground(context.getResources().getDrawable(R.drawable.border_selected));
            btnS4.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
        }));
        btnS4.setOnClickListener((view -> {
            select= 4;
            vm.setStat(select);
            btnS0.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS1.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS2.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS3.setBackground(context.getResources().getDrawable(R.drawable.border_no_selected));
            btnS4.setBackground(context.getResources().getDrawable(R.drawable.border_selected));
        }));

        btnCancel.setOnClickListener((view -> { dialog.dismiss(); }));

    dialog.show();
    }

    private int capItem=0;
    private String datess="No Date";
    private void preViewData(VideoModel vm, final int pos){
        final String[] STAT= context.getResources().getStringArray(R.array.stat);
        Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_DayNight_Dialog);
//        dialog = new Dialog(context,R.style.Animate3);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit_capitule);

        ImageView btnExit = (ImageView) dialog.findViewById(R.id.iv_dialog_cancel);
        ImageView btnSave = (ImageView) dialog.findViewById(R.id.ibtn_dialog_save);
        ImageView btnAdd = (ImageView) dialog.findViewById(R.id.ibtn_dialog_add);
        ImageView btnSustract = (ImageView) dialog.findViewById(R.id.ibtn_dialog_sustract);

        View iColor = (View) dialog.findViewById(R.id.v_dialog_color);
        ImageView ivImage = (ImageView) dialog.findViewById(R.id.iv_dialog_image);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_dialog_name);
        TextView tvStat = (TextView) dialog.findViewById(R.id.tv_dialog_stat);
        TextView tvCap = (TextView) dialog.findViewById(R.id.tv_dialog_cap);
        TextView tvDate = (TextView) dialog.findViewById(R.id.tv_dialog_date);

        datess=vm.getmDateC();
        capItem=vm.getCap();
        iColor.setBackgroundColor(Color.parseColor(vm.getmColor()));
        tvName.setText(""+ vm.getTitle());
        tvStat.setText(""+STAT[vm.getStat()]);
        tvCap.setText("cap: "+vm.getCap());
        tvDate.setText(""+vm.getmDateU());
        setHolderImage(vm.getImage64(),ivImage);

        btnExit.setOnClickListener((view -> { dialog.dismiss(); }));

        btnAdd.setOnClickListener((view -> {
            capItem=vm.getCap();
            if (vm.getCap()<9999) {
                capItem++;
                vm.setCap(capItem);
            }else
                Toast.makeText(context, "No se puede SUBIR el numero", Toast.LENGTH_SHORT).show();

            tvCap.setText("");
            tvCap.setText("cap: "+vm.getCap());
            tvDate.setText("");
            tvDate.setText(UtilsDate.dateTodayFormat("yyyy-MM-dd HH:mm:ss"));
            tvDate.setTextColor(Color.parseColor("#002fe7"));
        }));

        btnSustract.setOnClickListener((view -> {
            capItem=vm.getCap();
            if (capItem>0) {
                capItem--;
                vm.setCap(capItem);
            }else
                Toast.makeText(context, "No se puede Bajar el numero", Toast.LENGTH_SHORT).show();

            tvCap.setText("");
            tvCap.setText("cap: "+vm.getCap());
            tvDate.setText(UtilsDate.dateTodayFormat("yyyy-MM-dd HH:mm:ss"));
            tvDate.setTextColor(Color.parseColor("#002fe7"));
        }));

        btnSave.setOnClickListener(view -> {
            vm.setmDateU(tvDate.getText().toString());
            vm.setDateU(UtilsDate.Epoch(vm.getmDateU(),"yyyy-MM-dd HH:mm:ss"));
            vm.setCap(Integer.parseInt(tvCap.getText().toString().substring(5)));
            mData.set(pos,vm);
            refresh();

            AListDB aListDB = new AListDB(context);
            if (aListDB.getSizeDB()>0) aListDB.deleteAll();
            aListDB.setVideo(getList());
            Toast.makeText(context, "Item actualizado...", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });
        dialog.show();
    }

    private void setHolderImage(String mImage, ImageView mImageView) {
        try {
            if(!mImage.equals("")) {
                if (!mImage.contains("http")) {
                    if (!mImage.contains(" ")){
                        Bitmap bitmap = Utils.Base64ToBitmap(mImage);
                        bitmap = Utils.resizeImage(context,bitmap,1050,1050);
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
