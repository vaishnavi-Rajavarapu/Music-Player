package com.example.vmusicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView myLVsongs;
    String[] items;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myLVsongs=(ListView) findViewById(R.id.mySongListView);
        runtimepermission();
        display();
        FloatingActionButton fab = findViewById(R.id.plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                Intent intent=new Intent(MainActivity.this,addMore.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void runtimepermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) { }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.search){
            editText.setVisibility(View.VISIBLE);
            String song_searched="";
            song_searched=editText.getText().toString();
            if(song_searched==""){

            }
            else {

            }
        }
        return super.onOptionsItemSelected(item);
    }
    public ArrayList<File> findsong(File file){
        ArrayList<File> arrayList=new ArrayList<File>();

        try{

            File[] files=file.listFiles();
            for(File singleFile:files){
                if(singleFile.isDirectory() && !singleFile.isHidden())
                {
                    arrayList.addAll(findsong(singleFile));
                }
                else {
                    if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        arrayList.add(singleFile);
                    }
                }
            }}
        catch (NullPointerException ignored){}

        return arrayList;
    }

    void display(){
        final ArrayList<File> mysongs=findsong(Environment.getExternalStorageDirectory());

        try {
            items =new String[mysongs.size()];
            for(int i=0;i<mysongs.size();i++) {

                items[i] = mysongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
            }} catch (NullPointerException ignored) {
        }

        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        myLVsongs.setAdapter(myAdapter);
        myLVsongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname=myLVsongs.getItemAtPosition(i).toString();
                startActivity(new Intent(getApplicationContext(),Player.class).putExtra("songs",mysongs)
                        .putExtra("songname",songname)
                        .putExtra("pos",i));
            }
        });
    }

}
