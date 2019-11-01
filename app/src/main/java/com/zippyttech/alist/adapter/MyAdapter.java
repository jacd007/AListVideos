package com.zippyttech.alist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zippyttech.alist.model.SpnModel;
import com.zippyttech.alist.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<SpnModel> {
    private static final String TAG = "MyAdapter";
    private View mvColor;
    private Context mContext;
    private static ArrayList<SpnModel> listState;
    private MyAdapter myAdapter;
    private boolean isFromView = false;



    public MyAdapter(Context context, int resource, List<SpnModel> objects, View vColors) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<SpnModel>) objects;
        this.myAdapter = this;
        this.mvColor = vColors;
    }


    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.item_color_spinner, null);
            holder = new ViewHolder();
            holder.mColor = (View) convertView
                    .findViewById(R.id.i_spn_color);
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.tv_spn_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            int colors = Color.parseColor(listState.get(position).getmColor());
            String text = listState.get(position).getTitle();
                mvColor.setBackgroundColor(colors);
                holder.mColor.setBackgroundColor(colors);
                holder.mTextView.setText(text);
        }catch (Exception e){e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.mColor.setBackgroundColor(Color.BLACK);
            }
        }


        if (position<1){
            holder.mColor.setVisibility(View.GONE);
        }else {
            holder.mColor.setVisibility(View.VISIBLE);
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpnModel sm = listState.get(position);

            }
        });

        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private View mColor;
    }

}