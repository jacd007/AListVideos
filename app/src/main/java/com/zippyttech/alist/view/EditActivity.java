package com.zippyttech.alist.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.zippyttech.alist.MainActivity;
import com.zippyttech.alist.R;
import com.zippyttech.alist.adapter.MyAdapter;
import com.zippyttech.alist.common.Codes;
import com.zippyttech.alist.common.Upload;
import com.zippyttech.alist.common.Utils;
import com.zippyttech.alist.common.UtilsGson;
import com.zippyttech.alist.common.UtilsImage;
import com.zippyttech.alist.common.UtilsItemList;
import com.zippyttech.alist.model.ImageModel;
import com.zippyttech.alist.model.SpnModel;
import com.zippyttech.alist.model.VideoModel;
import com.zippyttech.datelib.DateUtils.UtilsDate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditActivity";

    private SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private List<VideoModel> list;
    private List<SpnModel> listSpn;
    private String[] COLOR_IN, COLOR_POS, COLOR_NAME, DAYS, TYPE, STAT,OTHERS;

    private CheckBox cbCloud;
    private ImageView ivImage;
    private TextView tvTitles, tvCap, tvDateC, tvDateU;
    private EditText etTitle, etCap;
    private Switch swTitle;
    private ImageButton ibtnAdd, ibtnSust;
    private Button btnSave, btnCancel;
    private Spinner spnStatus, spnNextDay, spnColor, spnType;
    private MyAdapter myAdapter;
    private VideoModel vm;
    private int POSITION;
    private boolean NEW_ITEM;
    private View vColors;


    private FirebaseDatabase mFirebaseDataBase;

    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ProgressBar mProgressBar;
    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initFirebase();
        initComponent();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mFirebaseDataBase = FirebaseDatabase.getInstance();

    }

    private void initComponent() {
        settings = getSharedPreferences(getResources().getString(R.string.SHARED_KEY), 0);
        editor = settings.edit();

        list = new ArrayList<>();
        vm = new VideoModel();
        NEW_ITEM = false;
        getDataBundle();

        COLOR_IN = getResources().getStringArray(R.array.colors_in);
        COLOR_POS = getResources().getStringArray(R.array.colors_pos);
        COLOR_NAME = getResources().getStringArray(R.array.colors_name);
        DAYS = getResources().getStringArray(R.array.days);
        TYPE = getResources().getStringArray(R.array.Type);
        STAT = getResources().getStringArray(R.array.stat);
        OTHERS = getResources().getStringArray(R.array.Others);

        mProgressBar = findViewById(R.id.progress_bar);
         cbCloud = (CheckBox) findViewById(R.id.cbFirebase);

        vColors = (View) findViewById(R.id.vItemColor);
        swTitle = (Switch) findViewById(R.id.swTitle);

        tvDateC = (TextView) findViewById(R.id.tvDateItemCr);
        tvDateU = (TextView) findViewById(R.id.tvDateItemUp);
        tvTitles = (TextView) findViewById(R.id.tvItemTitles);
        tvCap = (TextView) findViewById(R.id.tvItemCapitules);

        ivImage = (ImageView) findViewById(R.id.ivItemImage);

        etTitle = (EditText) findViewById(R.id.etItemTitle);
        etCap = (EditText) findViewById(R.id.etItemCapitule);

        ibtnAdd = (ImageButton) findViewById(R.id.ibtnAdd1);
        ibtnSust = (ImageButton) findViewById(R.id.ibtnSust1);

        btnSave = (Button) findViewById(R.id.btnItemSave);
        btnCancel = (Button) findViewById(R.id.btnItemCancel);

        if (NEW_ITEM) crear1Item();
        else writeData(list);

        spnStatus = (Spinner) findViewById(R.id.spnItemStatus);
        spnNextDay = (Spinner) findViewById(R.id.spnItemNextDay);
        spnColor = (Spinner) findViewById(R.id.spnItemColor);
        spnType = (Spinner) findViewById(R.id.spnItemType);

        ArrayAdapter<String> dAdapter;
        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, DAYS);
        dAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnNextDay.setAdapter(dAdapter);

        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, STAT);
        dAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnStatus.setAdapter(dAdapter);

        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, TYPE);
        dAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnType.setAdapter(dAdapter);

        listSpn = new ArrayList<>();
            SpnModel sm1 = new SpnModel();
            sm1.setPosition(0);
            sm1.setmColor(COLOR_POS[0]);
            sm1.setTitle(COLOR_NAME[0]);
            listSpn.add(sm1);

        for (int i=1;i<10;i++){
            SpnModel sm = new SpnModel();
            sm.setPosition(i+1);
            sm.setmColor(COLOR_POS[i]);
            sm.setTitle(COLOR_NAME[i]);
            listSpn.add(sm);
        }
        myAdapter = new MyAdapter(EditActivity.this, 0, listSpn,vColors);
        spnColor.setAdapter(myAdapter);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        ivImage.setOnClickListener(this);
        spnStatus.setOnItemSelectedListener(this);
        spnColor.setOnItemSelectedListener(this);
        spnNextDay.setOnItemSelectedListener(this);
        ibtnAdd.setOnClickListener(this);
        ibtnSust.setOnClickListener(this);
        etTitle.setOnClickListener(this);

        swTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etTitle.setVisibility(View.VISIBLE);
                    etCap.setVisibility(View.VISIBLE);
                    tvTitles.setVisibility(View.GONE);
                    tvCap.setVisibility(View.GONE);
                } else {
                    etTitle.setVisibility(View.GONE);
                    etCap.setVisibility(View.GONE);
                    tvTitles.setVisibility(View.VISIBLE);
                    tvCap.setVisibility(View.VISIBLE);
                }
            }
        });

        mFollowText(etTitle,tvTitles);
        mFollowText(etCap,tvCap);
        setPositionSpinner();
        cbCloud.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Toast.makeText(EditActivity.this, "Guardado en la nube.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EditActivity.this, "Guardado solo en local.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setPositionSpinner() {
        int posStat = vm.getStat();
        int posday = vm.getDay();
        int postype = vm.getType();
        int poscolor = vm.getColor();

        for (int i=0;i<(DAYS.length-1);i++){
            if ( DAYS[i].equals( vm.getmDay() ) ){
                posday = i;
                break;
            }
        }
        for (int i=0;i<(COLOR_POS.length-1);i++){
            if ( DAYS[i].equals( vm.getmDay() ) ){
                poscolor = i;
                break;
            }
        }

        spnStatus.setSelection(posStat);
        spnNextDay.setSelection(posday);
        spnType.setSelection(postype);
        spnColor.setSelection(poscolor);
    }

    private void writeData(List<VideoModel> list) {
//        VideoModel vm = new VideoModel();
       if(list.size()>0) vm = list.get(0);
        tvTitles.setText(vm.getTitle());
        tvCap.setText(""+vm.getCap());
        tvDateC.setText(vm.getmDateC());
        tvDateU.setText(vm.getmDateU());
        etTitle.setText(vm.getTitle());
        etCap.setText(""+vm.getCap());
//        vm.setmColor(COLOR_POS[8]);
        setImage(vm.getImage64());
    }

    private void setImage(String mImage){
        try {
            if (!mImage.equals("null")) {
                if (!mImage.substring(0, 4).equals("http")) {
                    if (!mImage.substring(0, 23).equals("data:image/jpeg;base64,")) {
                        mImage = "data:image/jpeg;base64," + mImage;
                    }
                    Bitmap bitmap = UtilsImage.b64ToBitmap(mImage);
                    ivImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 1250, 1250, false));
                } else {
                    Glide.with(this)
                            .load(mImage)
                            .placeholder(R.drawable.ic_broken_image)
                            .error(R.drawable.ic_no_image)
                            .into(ivImage);
                }
            }else {
                Glide.with(this)
                        .load("xxxx")
                        .error(R.drawable.ic_no_image)
                        .into(ivImage);
            }
        }catch (VerifyError e){
            ivImage.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_no_image));
            e.printStackTrace();
            Log.e("setmColor",e.getMessage());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            Glide.with(this)
                    .load(mImage)
                    .placeholder(R.drawable.ic_broken_image)
                    .error(R.drawable.ic_no_image)
                    .into(ivImage);
            Log.e("setmColor",e.getMessage());
        }catch (NullPointerException e){
            Log.e("setmColor",e.getMessage());
        }
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
        vm.setmColor(COLOR_IN[8]);
        vm.setmDay(DAYS[1]);
        vm.setmType(TYPE[3]);
        vm.setmStat(STAT[2]);
        vm.setStat(2);
        vm.setmOther(OTHERS[3]);
        list.add(vm);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        int capItem = (etCap.getText().toString().isEmpty()) ? 0 : Integer.parseInt(etCap.getText().toString());
        etCap.setText(""+capItem);

        switch (id) {
            case R.id.ivItemImage:
                LoadImage1(this);
                break;
            case R.id.ibtnAdd1:
                if (capItem<9999)
                    capItem++;
                else
                    Toast.makeText(this, "No se puede SUBIR el numero", Toast.LENGTH_SHORT).show();

                etCap.setText("");
                etCap.setText(""+capItem);
                break;
            case R.id.ibtnSust1:
                if (capItem>0)
                    capItem--;
                else
                    Toast.makeText(this, "No se puede Bajar el numero", Toast.LENGTH_SHORT).show();

                etCap.setText("");
                etCap.setText(""+capItem);
                break;
            case R.id.btnItemCancel:
                Intent i1 = new Intent();
                setResult(RESULT_CANCELED, i1);
                break;
            case R.id.btnItemSave:

                vm.setTag(UUID.randomUUID().toString());
                vm.setTitle(tvTitles.getText().toString());
                vm.setCap(Integer.parseInt(tvCap.getText().toString()));
                vm.setmStat(spnStatus.getSelectedItem().toString());
                vm.setmDateC(tvDateC.getText().toString());
                vm.setmDateU(UtilsDate.getDateToday());
                vm.setmDay(spnNextDay.getSelectedItem().toString());
                vm.setmType(spnType.getSelectedItem().toString());
                vm.setmOther(OTHERS[spnType.getSelectedItemPosition()]);

                vm.setStat(spnStatus.getSelectedItemPosition());
                vm.setDay(spnNextDay.getSelectedItemPosition());
                vm.setType(spnType.getSelectedItemPosition());
                vm.setColor(spnColor.getSelectedItemPosition());

                list = new ArrayList<>();
                list.add(vm);
                Intent i = new Intent();
                i.putExtra("position",POSITION);
                String jsonSTR = UtilsGson.ListToString(list);
                i.putExtra(UtilsItemList.myLIST, jsonSTR);

                Log.w(TAG,jsonSTR);

                if (Utils.isNetworkConnectionAvailable(this)) {

                    if (cbCloud.isChecked()) {
                        AutoSave(list);
                        if (mUploadTask != null && mUploadTask.isInProgress()) {
                            Toast.makeText(EditActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadFile();
                        }
                    }
                } else
                    Toast.makeText(this, "Internet Disabled, DB-firebase no connect.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK, i);
               finish();

                break;
        }

    }

    private void AutoSave(List<VideoModel> list) {
        VideoModel vm = list.get(0);
        ImageModel im = new ImageModel();
        im.setTag(vm.getTag());
        im.setTitle(vm.getTitle());
        im.setImage64(vm.getImage64());

        mDatabaseRef.child("Video").child(vm.getTag()).setValue(vm);
        mDatabaseRef.child("Imagenes").child(im.getTag()).setValue(im);
        Log.w(TAG,"Aqui debe borrrar y limpiar DB.....");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        switch (adapterView.getId()){
            case R.id.spnItemStatus:
                vm.setStat(adapterView.getSelectedItemPosition());
                vm.setmStat(spnStatus.getSelectedItem().toString());
                break;
            case R.id.spnItemType:
                vm.setType(adapterView.getSelectedItemPosition());
                vm.setmType(spnStatus.getSelectedItem().toString());
                break;
            case R.id.spnItemNextDay:

                vm.setmDay(spnNextDay.getSelectedItem().toString());
                break;
            case R.id.spnItemColor:
                Log.w(TAG,"color: "+ COLOR_POS[adapterView.getSelectedItemPosition()] );
                vm.setmColor(COLOR_POS[adapterView.getSelectedItemPosition()]);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        switch (adapterView.getId()){
            case R.id.spnItemStatus:
                vm.setStat(adapterView.getSelectedItemPosition());
                vm.setmStat(spnStatus.getSelectedItem().toString());
                break;
            case R.id.spnItemType:
                vm.setType(adapterView.getSelectedItemPosition());
                vm.setmType(spnStatus.getSelectedItem().toString());
                break;
            case R.id.spnItemNextDay:

                vm.setmDay(spnNextDay.getSelectedItem().toString());
                break;
            case R.id.spnItemColor:

                vm.setmColor(COLOR_POS[adapterView.getSelectedItemPosition()]);
                break;
        }
    }


    private void getDataBundle() {

        try {
            POSITION = 0;
            Bundle parametros = this.getIntent().getExtras();
            if (parametros != null) {
                NEW_ITEM = parametros.getBoolean("new");
                String date = parametros.getString(UtilsItemList.myLIST);
                POSITION = parametros.getInt("position");
                Log.w(TAG, "List item: " + date);

                list = UtilsGson.StringToList(date);
//                Log.w(TAG,"image bundle: "+list.get(0).getImage64());
            } else {
                Log.w(TAG, "parametros nulos");
            }
        } catch (NullPointerException ee) {
            ee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size()>0)
            vm = list.get(0);
    }

    private void mFollowText(final EditText editText, final TextView tuTextView){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.w(TAG,"resp: "+s);
                if(editText.getText().length() != 0) {
                    tuTextView.setText(s.toString());
                }else{
                    tuTextView.setText("<Sin titulo>");
                }
            }
        });
    }

    private void LoadImage1(final Context context){

        final CharSequence[] opciones={"Vista Previa","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(context);
        alertOpciones.setTitle("Seleccione una opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(opciones[i].equals("Vista Previa")){
//                    Toast.makeText(context, "No Disponible", Toast.LENGTH_SHORT).show();
                    Preview();
                }
                else if(opciones[i].equals("Cargar Imagen")){
                    try {
                        loadImageEst();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    dialogInterface.dismiss();
                }

            }
        });
        alertOpciones.show();
    }

    private void Preview() {
        DialogView.preViewImage(this,vm,true);
    }

    private void loadImageEst() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else{
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Seleccione la aplicación"),Codes.GaleryToEdit);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Error al abrir la galeria...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(EditActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
//                                    Log.w(TAG,"URL:::: "+photoLink);
                                    Upload upload = new Upload(etTitle.getText().toString().trim(),
                                            taskSnapshot.getMetadata().getReference().getDownloadUrl().toString() );
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent,"Seleccione la aplicación"),Codes.GaleryToEdit);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Error en el permiso de almacenamiento...", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Permiso de lectura de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }

        }
    }

    String strB64;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (data!=null) {
            if ((resultCode == RESULT_OK) && (requestCode== Codes.GaleryToEdit)) {
                 mImageUri = data.getData();
                Glide.with(this)
                        .load(mImageUri)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_no_image)
                        .into(ivImage);
//                ivPhoto.setImageURI(mImageUri);
                String strDir = "file/"+ mImageUri.toString().substring(8)+".png";
                try {
                    Log.w(TAG,"pollo: "+strDir);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    strB64 = UtilsImage.bitmapToBase64(bitmap);
                    vm.setImage64(strB64);
                    Log.w(TAG,"JSON EDITAR galeria: "+strB64.length()+" char");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

}
