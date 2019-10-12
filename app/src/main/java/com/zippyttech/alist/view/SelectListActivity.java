package com.zippyttech.alist.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zippyttech.alist.R;
import com.zippyttech.alist.adapter.ListAdapter;
import com.zippyttech.alist.common.Codes;
import com.zippyttech.alist.common.UtilsGson;
import com.zippyttech.alist.common.UtilsItemList;
import com.zippyttech.alist.data.AListDB;
import com.zippyttech.alist.model.VideoModel;
import com.zippyttech.datelib.DateUtils.UtilsDate;

import java.util.ArrayList;
import java.util.List;

public class SelectListActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "SelectListActivity";

    private com.zippyttech.alist.adapter.ListAdapter adapter;
    private LinearLayoutManager llm;

    private RecyclerView rv;
    private ImageButton ibtnAdd;
    private ImageButton ibtnSave;
    private ImageButton ibtnSend;
    private TextView tvCount;
    private Context mContext;

    private List<VideoModel> list;
    private VideoModel vm;
    private String[] COLOR_IN,COLOR_POS;
    private String[] DAYS;
    private String[] TYPE;
    private String[] STAT;
    private AppCompatActivity acti;

    private AListDB aListDB;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_list);
        initComponent();
        initFirebase();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void initComponent() {
        aListDB = new AListDB(this);
        mContext = SelectListActivity.this;
        acti = (AppCompatActivity) SelectListActivity.this;
        rv = (RecyclerView) findViewById(R.id.rv_List);
        ibtnAdd = (ImageButton) findViewById(R.id.ibtn_list_add);
        ibtnSave = (ImageButton) findViewById(R.id.ibtn_list_save);
        ibtnSend = (ImageButton) findViewById(R.id.ibtn_list_send);
        tvCount = (TextView) findViewById(R.id.tv_list_exit);

        ibtnAdd.setOnClickListener(this);
        ibtnSave.setOnClickListener(this);
        ibtnSend.setOnClickListener(this);
        tvCount.setOnClickListener(this);

        list = new ArrayList<>();
        COLOR_IN = getResources().getStringArray(R.array.colors_in);
        COLOR_POS = getResources().getStringArray(R.array.colors_pos);
        DAYS = getResources().getStringArray(R.array.days);
        TYPE = getResources().getStringArray(R.array.Type);
        STAT = getResources().getStringArray(R.array.stat);

        list = aListDB.getVideo();
//        crearItem();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.ibtn_list_add:
//                    crear1Item();
                int count = (list.size() > 0) ? adapter.getItemCount() : 0;
                    Intent newItem = new Intent(this,EditActivity.class);
                    newItem.putExtra("new",true);
                    newItem.putExtra("position",count);
                    startActivityForResult(newItem,Codes.EditNewToSelectList);
                break;

            case R.id.ibtn_list_save:

                break;

            case R.id.ibtn_list_send:

                break;

            case R.id.tv_list_exit:

                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) SelectListActivity.this.getSystemService(Context.SEARCH_SERVICE);

        android.widget.SearchView searchView = null;
        if (searchItem != null) {
            searchView = (android.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SelectListActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(SelectListActivity.this);
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);

        }
        return super.onCreateOptionsMenu(menu);
    }

    private void print(List<VideoModel> LIST) {
        llm = new LinearLayoutManager(mContext);
        rv.setLayoutManager(llm);
        adapter = new ListAdapter(LIST,this, acti,tvCount);
        rv.setAdapter(adapter);
        tvCount.setText(""+adapter.getItemCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<VideoModel> auxList = new ArrayList<>();
        Log.w("onActivityResult","-------------------------------------------------------------------------------------------");
        try {
            if (resultCode == RESULT_OK){
                switch (requestCode){
                    case Codes.EditToSelectList:
                        Log.w("onActivityResult","requestCode "+ Codes.EditToSelectList);
                        if (data != null){
                            String dateList = data.getStringExtra(UtilsItemList.myLIST);
                            Log.w("onActivityResult","data "+ dateList);
                            int position = data.getIntExtra("position",0);
                            auxList = UtilsGson.StringToList(dateList);
                            try{
                                adapter.replaceItem( UtilsGson.StringToList(dateList) , position );
//                                aListDB.updateData(a0, auxList);
                            }catch (NullPointerException e){e.printStackTrace();}

                            AutoSave();
                            Log.w("onActivityResult","data: "+auxList);
                        }else {
                            Log.w("onActivityResult","no data");
                        }
                        break;
                    case Codes.EditNewToSelectList:
                        Log.w("onActivityResult","requestCode "+ Codes.EditToSelectList);
                        if (data != null){
                            String dateList = data.getStringExtra(UtilsItemList.myLIST);
                            Log.w("onActivityResult","data "+ dateList);
                            auxList = UtilsGson.StringToList(dateList);
                            list.addAll(auxList);
                            try {
                               print(list);
                               aListDB.setVideo(auxList);
                            }catch (NullPointerException e){e.printStackTrace();}

                            AutoSave();
                            Log.w("onActivityResult","data: "+auxList);
                        }else {
                            Log.w("onActivityResult","no data");
                        }
                        break;
                    case Codes.GaleryToEdit:
                        Log.w("onActivityResult","no data");
                        break;
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, "No se pudo realizar esta operación.", Toast.LENGTH_SHORT).show();
        }
    }

    private void AutoSave() {
        Log.w(TAG,"Aqui debe borrrar y limpiar DB.....");
    }




    private void crearItem() {

        list = new ArrayList<>();

        for (int i=0;i<4;i++){
            vm = new VideoModel();
            vm.setId(i);
            vm.setTitle("title "+(i+1));
            vm.setCap(i);
            vm.setDateC( UtilsDate.Epoch( UtilsDate.getDateToday(),"yyyy-MM-dd HH:mm:ss") );
            vm.setmDateC( UtilsDate.getDateToday() );
            vm.setDateU( UtilsDate.Epoch( UtilsDate.getDateToday(),"yyyy-MM-dd HH:mm:ss") );
            vm.setmDateU( UtilsDate.getDateToday() );
            vm.setmColor(COLOR_IN[i+1]);
            vm.setmDay(DAYS[i+1]);
            vm.setmType(TYPE[i+1]);
            vm.setmStat(STAT[1]);
            vm.setStat(1);
            vm.setImage64("http://taiga.zippyttech.com/media/user/3/9/4/7/3397fdcf874e7720dd942bc864db0a0c486e665b51b1d00dae358af9fd8c/windowsappleandroid.jpg.80x80_q85_crop.jpg");
            vm.setmOther("Cap:");

            list.add(vm);
        }
        print(list);
    }

    private void crear1Item() {

        vm = new VideoModel();
        vm.setId(list.size()+1);
        vm.setTitle("title "+(list.size()+1));
        vm.setCap(list.size());
        vm.setDateC( UtilsDate.Epoch( UtilsDate.getDateToday(),"yyyy-MM-dd HH:mm:ss") );
        vm.setmDateC( UtilsDate.getDateToday() );
        vm.setDateU( UtilsDate.Epoch( UtilsDate.getDateToday(),"yyyy-MM-dd HH:mm:ss") );
        vm.setmDateU( UtilsDate.getDateToday() );
        vm.setmColor(COLOR_IN[2]);
        vm.setmDay(DAYS[1]);
        vm.setmType(TYPE[3]);
        vm.setmStat(STAT[2]);
        vm.setStat(2);
//            vm.setmColor("");
        vm.setmOther("Temp:");

        list.add(vm);

        print(list);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        List<VideoModel> list_aux = new ArrayList<>();
        list = adapter.getList();
//        list = animesDB.getVideo();

        for (VideoModel aux:  list ) {
            try {
                String name = "",cap="0",status="";

                name = aux.getTitle().toLowerCase();
                cap = String.valueOf(aux.getCap());
                status = aux.getmStat().toLowerCase();

                String text = s.toLowerCase();
//                Log.w(TAG,">> "+name+" = "+text);

                if (name.contains(text))
                    list_aux.add(aux);
                else if (cap.contains(text))
                    list_aux.add(aux);
                else if (status.contains(text))
                    list_aux.add(aux);
                else
                    Log.w("onQueryTextChange","nada");

            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
        }

        adapter.refresh(list_aux);
        return false;
    }
}
