package com.example.rithuusharrma.tunes;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Tunes extends AppCompatActivity {
    ListView listView;
    Boolean mExternalStorageAvailable,permission=false;
    String[] items;//to read all files
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tunes);

        listView = (ListView) findViewById(R.id.listView);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = checkStoragePermission();
        }
        else
            display();
        if(permission){
            display();
        }
    }


        public ArrayList<File> findSong(File root){
            ArrayList<File> at = new ArrayList<File>();
            File[] files = root.listFiles();

            for(File singleFile : files){
                if(singleFile.isDirectory() && !singleFile.isHidden()){
                    at.addAll(findSong(singleFile));
                }
                else{
                    if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        at.add(singleFile);
                    }
                }
            }
            return at;
        }

    void display(){

        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[ mySongs.size() ];
        for(int i=0;i<mySongs.size();i++){
            //toast(mySongs.get(i).getName().toString());
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("pos",position).putExtra("songs",mySongs));
            }
        });
    }
    void checkExternalStorage(){
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mExternalStorageAvailable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                mExternalStorageAvailable = true;
            } else {
                mExternalStorageAvailable= false;
            }
            handleExternalStorageState();

    }
    void handleExternalStorageState() {
        if(mExternalStorageAvailable){
            display();
        }
        else{
            Toast.makeText(getApplicationContext(),"Please insert an SDcard",Toast.LENGTH_LONG).show();
        }
    }
    public boolean checkStoragePermission() {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                                checkExternalStorage();
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}