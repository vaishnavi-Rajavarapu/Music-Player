package com.example.vmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.text.TextUtils.indexOf;

public class Player extends AppCompatActivity {
    Button btn_nxt,btn_prev,btn_puas,voicebtn;
    TextView songTextLable;
    SeekBar sb;
    static MediaPlayer myMediaPlayer;
    int position;
    String mode="OFF";
    String pusenode="on";
    ArrayList<File> mysongs;
    Thread updateseekbar;
    String sname;
    LinearLayout playlay;
    RelativeLayout visibl;
    private String keeper="";
    private  static final int REQUEST_RECORD_PERMISSION=100;
    private SpeechRecognizer speechRecognizer=null;
    private Object view;
    private Intent speechrecognizerintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btn_nxt=(Button) findViewById(R.id.next);
        btn_prev=(Button) findViewById(R.id.previous);
        btn_puas=(Button) findViewById(R.id.pause);
        songTextLable=(TextView) findViewById(R.id.name);
        //input=(TextView) findViewById(R.id.tv);
        sb=(SeekBar) findViewById(R.id.seekBar);
        visibl=findViewById(R.id.visbl);
        voicebtn=findViewById(R.id.vbtn);
        checkVoiceCommandPermissions();
        playlay=findViewById(R.id.play);
        //result=findViewById(R.id.tv);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateseekbar=new Thread(){
            @Override
            public void run() {
                super.run();
                int totalduration=myMediaPlayer.getDuration();
                int currentposition=0;
                while (currentposition<totalduration){
                    try{
                        sleep(500);
                        currentposition=myMediaPlayer.getCurrentPosition();
                        sb.setProgress(currentposition);
                    }
                    catch (InterruptedException e)
                    {e.printStackTrace();}
                }
                if(currentposition==totalduration){
                    nextaction();
                }
            }
        };
        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mysongs=(ArrayList) bundle.getParcelableArrayList("songs");
        sname=mysongs.get(position).getName().toString();
        String songName=i.getStringExtra("songname");
        songTextLable.setText(songName);
        songTextLable.setSelected(true);
        position=bundle.getInt("pos",0);

        Uri u=Uri.parse(mysongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        myMediaPlayer.start();
        sb.setMax(myMediaPlayer.getDuration());
        updateseekbar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        //sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btn_puas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sb.setMax(myMediaPlayer.getDuration());
                if(myMediaPlayer.isPlaying()){
                    btn_puas.setBackgroundResource(R.drawable.play);
                    myMediaPlayer.pause();
                    pusenode="on";
                }
                else {
                    btn_puas.setBackgroundResource(R.drawable.pause);
                    myMediaPlayer.start();
                    pusenode="off";

                }

            }
        });


        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextaction();
            }



        });
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousaction();

            }
        });

        voicebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("ON")) {
                    mode = "OFF";
                    voicebtn.setText("voice enabled mode:OFF");
                    visibl.setVisibility(View.VISIBLE);
                    speechRecognizer.stopListening();

                } else {
                    mode = "ON";
                    voicebtn.setText("voice enabled mode:ON");
                    visibl.setVisibility(View.GONE);


                }
            }

        });

        //private LinearLayout playlay;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(Player.this);
        speechrecognizerintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechrecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        speechrecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechrecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechrecognizerintent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesfound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesfound != null) {
                    keeper = matchesfound.get(0);
                    if(mode.equals("ON")){
                        if (keeper.equals("pause the song") || keeper.equals("pause") || keeper.equals("pause song")) {
                            sb.setMax(myMediaPlayer.getDuration());
                            if (myMediaPlayer.isPlaying()) {
                                btn_puas.setBackgroundResource(R.drawable.play);
                                myMediaPlayer.pause();
                                pusenode = "on";
                            } else {
                                Toast.makeText(Player.this, "it is already in pause state", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (keeper.equals("play the next song") || keeper.equals("next song") || keeper.equals("play next song")) {
                            nextaction();
                        }
                        else if (keeper.equals("play the previous song") || keeper.equals("previous") ||
                                keeper.equals("play previous song")) {
                            previousaction();
                        }
                        else if (keeper.equals("play the song") || keeper.equals("play") || keeper.equals("play song")) {
                            sb.setMax(myMediaPlayer.getDuration());
                            if (myMediaPlayer.isPlaying()) {
                                Toast.makeText(Player.this, "it is already playing", Toast.LENGTH_SHORT).show();
                            } else {
                                btn_puas.setBackgroundResource(R.drawable.pause);
                                myMediaPlayer.start();
                                pusenode = "off";

                            }
                        }
                        else{
                            Toast.makeText(Player.this, "Result: " + keeper, Toast.LENGTH_SHORT).show();}
                    }}
            }


            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        playlay.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechrecognizerintent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });

    }

    public void checkVoiceCommandPermissions(){
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(Player.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
            {
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
    public void nextaction()
    {
        if(pusenode.equals("off")){
            myMediaPlayer.stop();
            myMediaPlayer.release();
            position=((position+1)%mysongs.size());

            Uri u=Uri.parse(mysongs.get(position).toString());
            myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
            sname=mysongs.get(position).getName().toString();
            songTextLable.setText(sname);
            myMediaPlayer.start();}
        else{
            myMediaPlayer.stop();
            myMediaPlayer.release();
            position=((position+1)%mysongs.size());

            Uri u=Uri.parse(mysongs.get(position).toString());
            myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
            sname=mysongs.get(position).getName().toString();
            songTextLable.setText(sname);
        }

    }
    public void previousaction()
    {
        if(pusenode.equals("off")){
            myMediaPlayer.stop();
            myMediaPlayer.release();
            position=((position-1)<0)?(mysongs.size()-1):(position-1);
            Uri uri=Uri.parse(mysongs.get(position).toString());
            myMediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            sname=mysongs.get(position).getName().toString();
            songTextLable.setText(sname);
            myMediaPlayer.start();}
        else{
            myMediaPlayer.stop();
            myMediaPlayer.release();
            position=((position-1)<0)?(mysongs.size()-1):(position-1);
            Uri u=Uri.parse(mysongs.get(position).toString());
            myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
            sname=mysongs.get(position).getName().toString();
            songTextLable.setText(sname);
        }
    }

}
