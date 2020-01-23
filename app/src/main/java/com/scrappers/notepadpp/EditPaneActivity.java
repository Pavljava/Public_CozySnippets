package com.scrappers.notepadpp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;

import static android.os.Environment.getExternalStorageDirectory;
import static com.scrappers.notepadpp.MainActivity.fileName;
import static com.scrappers.notepadpp.MainActivity.finalOutText;
import static com.scrappers.notepadpp.MainActivity.recordName;
import static com.scrappers.notepadpp.MainActivity.refer;


interface EditPaneActivityUI{
    void vibrator(Vibrator v);
    void defineNotificationChannelID(String CHANNEL_ID);
    void ShowControlPadFragment(boolean bool);
    void InfinteCheckForPLayPauseBTN();
}

public class EditPaneActivity extends AppCompatActivity implements EditPaneActivityUI{

    //Instances(Components Objects)
    public static EditText ed;
    public EditText ed1;
    public TextView Duration;
    public TextView CurrentPositionOfMedia;
    FloatingActionButton playPause;
    static  FloatingActionButton recordStop;
    FloatingActionButton pasueRecord;
    FloatingActionButton speakBtn;
    SeekBar MediaSeekbar;
    AlertDialog d;

    public TextToSpeech ttsEngine;
    public NotificationManager nm;
    public NotificationCompat.Builder notifBuilder;
    public PendingIntent pendingIntent;
    static MediaRecorder record;
    MediaPlayer playRecord;

    //Reference Points
    public int SPEAK_STATE = 1;
    public static int VALUE_OF_RECORD = 1;
    public int PAUSE_VALUE = 1;

    boolean stopSimulate = false;

    String text = null;
    public static String pathForRecordings;
    public static String NewFileName;
    public static String path;
    public static boolean okPressed=false;
    String spancolor,textcolor;
    boolean recordingNow=false;

    //.....................
    static String handleRecordText_Notification=null;
    static  int recordSeconds=0;
    static int recordMinutes=0;
    static int recordHours=0;
    static Handler handlerForRecordDuration=new Handler();
    static TextView record_duration_txt;
    static String recordHoursString;
    static String recordMinutesString;
    static String recordSecondsString;
    //Handle the Progression of the SeekBar in a runOnUiThread >>>
    public Handler handler = new Handler();
    boolean btnBack=false;


    public void Notify(String title, String Messege, int icon, String CHANNEL_ID, int notifyId, String packagename) {
        vibrator((Vibrator) getSystemService(VIBRATOR_SERVICE));

        //create a Custom Notification
        RemoteViews notificationMinimizedState = new RemoteViews(getPackageName(), R.layout.shrinked_notification);
        RemoteViews notificationExapndState = new RemoteViews(getPackageName(), R.layout.expanded_notification);


//        Intent i=new Intent("NotifyBtn.stop");
//        PendingIntent Pi= PendingIntent.getBroadcast(getApplicationContext(),22,i,PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationExapndState.setOnClickPendingIntent(R.id.StoprecordingNotif,Pi);
//
//        notificationExapndState.setOnClickPendingIntent(R.id.PauserecordingNotif,Pi);
//
//        Intent intent = new Intent(getApplicationContext(), className.getClass());
//        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationMinimizedState)
//                .setCustomBigContentView(notificationExapndState)
                .setCustomHeadsUpContentView(notificationMinimizedState)
                .setContentTitle(title)
                .setContentText(Messege)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false);
        nm.notify(notifyId, notifBuilder.build());


    }



    public void vibrator(Vibrator v) {
        v.vibrate(20);
    }


    public void defineNotificationChannelID(String CHANNEL_ID) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }


    @SuppressLint("RestrictedApi")
    public void ShowControlPadFragment(boolean bool){

            //Define the fragemnt container id & Val
        final FrameLayout frameLayout=findViewById(R.id.layoutfrag);
        //Fragment & FragmentTransaction
        keyBoardControl key= new keyBoardControl();
        FragmentTransaction fm=getSupportFragmentManager().beginTransaction();
//        fm.setCustomAnimations(R.anim.fab_slide_in_from_left,R.anim.fab_slide_in_from_right);

        //show the current fragment in that frameLayout Container
        fm.replace(R.id.layoutfrag, key);
        //apply Changes
        fm.commit();



        if(bool == true ){


            frameLayout.setVisibility(View.VISIBLE);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fab_scale_up));
            ed.setFocusable(true);

        }else if(bool==false){

            frameLayout.setVisibility(View.INVISIBLE);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
            ed.setFocusable(false);

        }


      final com.google.android.material.floatingactionbutton.FloatingActionButton fragbtn=findViewById(R.id.closeKeyBoardController);

       fragbtn.setVisibility(View.VISIBLE);
        fragbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (frameLayout.getVisibility() == View.VISIBLE){
                    frameLayout.setVisibility(View.INVISIBLE);
                    frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_scale_up));



                    fragbtn.setImageResource(R.drawable.backwardarrow);

                }else {
                    frameLayout.setVisibility(View.VISIBLE);
                    frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_scale_up));

                    fragbtn.setImageResource(R.drawable.forwardarrow);

                }
            }
        });

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pane);

        AccessWritePermssions();

        defineNotificationChannelID("ID2020");
        defineNotificationChannelID("ID3030");
//        startService(new Intent(this, BackgroundService.class));


        if ( refer == 1 ){
            ed = findViewById(R.id.edText);
            ed.setText(finalOutText);
            DefineExistingRecording(recordName);

        } else {
            ed = findViewById(R.id.edText);

        }


        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ShowControlPadFragment(hasFocus);

            }
        });




    }


    public void InfinteCheckForPLayPauseBTN() {
        //Using an action listener
        try {
            playRecord.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {


                    playPause.setLabelText("Play Record Again");

                    Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.play_ico);
                    playPause.setImageDrawable(img);

                    stopSimulate = false;
                    nm.cancelAll();

                }
            });

        } catch (Exception error) {
            Toast.makeText(getApplicationContext(), "No Record to declare !", Toast.LENGTH_LONG).show();
            hideMediaComponents();
        }
    }


    @Override
    public void onBackPressed() {
        try {
            stopRecording();
            playRecord.stop();
            playRecord.release();
            //dismiss this callback UI Event Handler
            handler.removeCallbacksAndMessages(null);
            //save the notes
            writeTextFiles(getApplicationContext().getFilesDir() + "/" + fileName, ed.getText().toString());
            Toast.makeText(getApplicationContext(), "Note  Saved with the same Name !", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        btnBack=true;
        DialogBoxWithoutRecordSave();

    }


    //write Text Files to External Storage
    protected void writeTextFiles(String path, String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(data);
            Toast.makeText(getApplicationContext(), "Save Successful", Toast.LENGTH_LONG).show();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void DialogBoxWithRecordSave() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialogboxlayout, null);
        Button okbtn = layout.findViewById(R.id.okBtn);
        Button cancelbtn = layout.findViewById(R.id.CancelBtn);
        Button saveNameBtn = layout.findViewById(R.id.saveName);
        builder.setView(layout);
        // Dialog will have "Make a selection" as the title

        if ( refer == 0 ){
            saveNameBtn.setVisibility(View.INVISIBLE);

        } else {
            saveNameBtn.setVisibility(View.VISIBLE);
        }


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed1 = layout.findViewById(R.id.nameofsavefile);
                NewFileName = ed1.getText().toString();
                if ( !NewFileName.isEmpty() ){
                    path = getApplicationContext().getFilesDir() + "/" + NewFileName;
                    writeTextFiles(path, ed.getText().toString());
                    pathForRecordings = getExternalStorageDirectory() + "/SPRecordings/" + NewFileName
                            + ".mp3";

                    DefineNewRecord(pathForRecordings);
                    okPressed=true;

                    refer=1;
                    d.dismiss();

                } else {
                    long millis = System.currentTimeMillis();
                    Date date = new Date(millis);
                    path = getApplicationContext().getFilesDir() + "/" + "Recorded IN " + date.getDate() + "-" + date.getMonth() + "-" + (date.getYear() + 1900)
                            + " AT " + date.getHours() + ";" + date.getMinutes() + ";" + date.getSeconds();
                    writeTextFiles(path, ed.getText().toString());
                    pathForRecordings = getExternalStorageDirectory() + "/SPRecordings/" + "Recorded IN " + date.getDate() + "-" + date.getMonth() + "-" + (date.getYear() + 1900)
                            + " AT " + date.getHours() + ";" + date.getMinutes() + ";" + date.getSeconds() + ".mp3";

                    DefineNewRecord(pathForRecordings);
                    okPressed=true;

                    d.dismiss();
                }


            }
        });


        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Note & Record Not Saved !", Toast.LENGTH_LONG).show();
                VALUE_OF_RECORD = 1;
                d.dismiss();
            }
        });


        saveNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTextFiles(getApplicationContext().getFilesDir() + "/" + fileName, ed.getText().toString());
                pathForRecordings = getExternalStorageDirectory() + "/SPRecordings/" + fileName
                        + ".mp3";
                DefineNewRecord(pathForRecordings);
                okPressed=true;
                d.dismiss();
            }
        });

        builder.setCancelable(false);
        d = builder.create();

        d.getWindow().setGravity(Gravity.CENTER);
        d.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation2;


        d.show();

    }


    public void DialogBoxWithoutRecordSave() {


        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialogboxlayout, null);
        Button okbtn = layout.findViewById(R.id.okBtn);
        Button cancelbtn = layout.findViewById(R.id.CancelBtn);
        Button saveNameBtn = layout.findViewById(R.id.saveName);

        builder.setView(layout);
        // Dialog will have "Make a selection" as the title

        if ( refer == 0 ){
            saveNameBtn.setVisibility(View.INVISIBLE);

        } else {
            saveNameBtn.setVisibility(View.VISIBLE);
        }


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed1 = layout.findViewById(R.id.nameofsavefile);
                NewFileName = ed1.getText().toString();
                if ( !NewFileName.isEmpty() ){
                    path = getApplicationContext().getFilesDir() + "/" + NewFileName;
                    writeTextFiles(path, ed.getText().toString());
                    refer=1;
                    d.dismiss();


                    //restart to refresh
                    startActivity(new Intent(getApplicationContext(),MainActivity.class) );
                    finish();

                } else {
                    long millis = System.currentTimeMillis();
                    Date date = new Date(millis);
                    path = getApplicationContext().getFilesDir() + "/" + "Recorded IN " + date.getDate() + "-" + date.getMonth() + "-" + (date.getYear() + 1900)
                            + " AT " + date.getHours() + ";" + date.getMinutes() + ";" + date.getSeconds();
                    writeTextFiles(path, ed.getText().toString());

                    d.dismiss();


                    //restart to refresh
                    startActivity(new Intent(getApplicationContext(),MainActivity.class) );
                    finish();
                }
            }
        });


        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnBack == true){
                    Toast.makeText(getApplicationContext(), "Note Not Saved !", Toast.LENGTH_LONG).show();
                    d.dismiss();
                    //Back to the MainActivity class
                    startActivity(new Intent(getApplicationContext(),MainActivity.class) );
                    finish();
                    btnBack=false;

                }else{
                    Toast.makeText(getApplicationContext(), "Note Not Saved !", Toast.LENGTH_LONG).show();
                    d.dismiss();
                }
            }
        });


        saveNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTextFiles(getApplicationContext().getFilesDir() + "/" + fileName, ed.getText().toString());
                Toast.makeText(getApplicationContext(), "Note  Saved with the same Name !", Toast.LENGTH_LONG).show();
                d.dismiss();


                //restart to refresh
                startActivity(new Intent(getApplicationContext(),MainActivity.class) );
                finish();
            }
        });


        builder.setCancelable(true);
        d = builder.create();

        d.getWindow().setGravity(Gravity.CENTER);
        d.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation2;

        d.show();
    }



    public void AccessMicPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                11);
    }


    public void DefineNewRecord(String recordnameLocal) {

        recordStop = findViewById(R.id.Recordbtn);

        try {
            record = null;
            record = new MediaRecorder();
            record.reset();
            record.setAudioSource(MediaRecorder.AudioSource.MIC);
            record.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            record.setOutputFile(recordnameLocal);
            record.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(getApplicationContext(), "Mic Permission Denied", Toast.LENGTH_LONG).show();

        }

        try {
            record.prepare();
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please use a Simplified Name", Toast.LENGTH_LONG).show();

        }

        try {
            record.start();

            //to refresh values
            RefreshRecordingValues();
            Handle_Record_Duration();
            nm.cancelAll();
            Notify("Recording Lecture", recordnameLocal, R.drawable.ic_launcher_foreground, "ID2020", 2020, getPackageName());

            Toast.makeText(getApplicationContext(), "Start Recording on", Toast.LENGTH_LONG).show();
            recordStop.setLabelText(text);


        } catch (IllegalStateException error) {
            error.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error Mic is Busy", Toast.LENGTH_LONG).show();
            VALUE_OF_RECORD = 1;

        }

    }



    public void RefreshRecordingValues(){
        recordSeconds=0;
        recordHours=0;
        recordMinutes=0;
        handleRecordText_Notification=null;

    }

    public void stopRecording() {
        try {
            record.stop();
            record.release();
            record = null;
            recordingNow=false;

            DefineExistingRecording(pathForRecordings);
            PAUSE_VALUE = 2;
            pasueRecord.setLabelText("Pause");
            Drawable imgPause = getApplicationContext().getResources().getDrawable(R.drawable.pause_ico);
            pasueRecord.setImageDrawable(imgPause);

            showMediaComponents();


        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    public void playRecordingOrPause(View v) {
        InfinteCheckForPLayPauseBTN();
        playPause = findViewById(R.id.playPause);
        MediaSeekbar = findViewById(R.id.MediaSeekBar);
        Duration = findViewById(R.id.Duration);
        CurrentPositionOfMedia = findViewById(R.id.CurrentProgress);

        handlerForRecordDuration.removeCallbacksAndMessages(null);

        if(recordingNow ==false){
            try {
                if ( stopSimulate == false ){
                    playRecord.start();
                    update();

                    showMediaComponents();

                    getDurationOfPlayMedia();


                    Snackbar.make(v, "Playing Record", Snackbar.LENGTH_LONG).show();
                    stopSimulate = true;
                    playPause.setLabelText("Pause");

                    Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.pause_ico);
                    playPause.setImageDrawable(img);

                } else {
                    playRecord.pause();
                    Snackbar.make(v, "Pausing Record play", Snackbar.LENGTH_SHORT).show();
                    stopSimulate = false;
                    playPause.setLabelText("Play");
                    Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.play_ico);
                    playPause.setImageDrawable(img);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(v, "No Record to declare !", Snackbar.LENGTH_LONG).show();
                hideMediaComponents();

            }
        }
    }

    public void getDurationOfPlayMedia() {

        String out = "";
        // get mp3 info

        // convert duration to minute:seconds

        long dur = playRecord.getDuration();
        String seconds = String.valueOf((dur % 60000) / 1000);

        String minutes = String.valueOf(dur / 60000);
        out = minutes + ":" + seconds;
        if ( seconds.length() == 1 ){
            Duration.setText("0" + minutes + ":0" + seconds);
        } else {
            Duration.setText("0" + minutes + ":" + seconds);
        }

    }


    public void getCurrentProgressOfPlayMedia() {
        // load data file

        String out = "";
        // get mp3 info

        // convert duration to minute:seconds

        long dur = playRecord.getCurrentPosition();

        long secondsLong = (dur % 60000) / 1000;

        String seconds = String.valueOf(secondsLong);


        String minutes = String.valueOf(dur / 60000);


        out = minutes + ":" + seconds;
        if ( seconds.length() == 1 ){
            CurrentPositionOfMedia.setText("0" + minutes + ":0" + seconds);
        } else {
            CurrentPositionOfMedia.setText("0" + minutes + ":" + seconds);
        }
    }



    public  void Handle_Record_Duration(){
        record_duration_txt=findViewById(R.id.recordduration);
        showRecordComponent();
        EditPaneActivity.this.runOnUiThread(new Runnable() {

            public void run() {
                if ( record != null ){
                    try {
                        recordSeconds++;
                        if(recordSeconds >=60){
                            ++recordMinutes;
                            recordSeconds=0;
                            handleRecordText_Notification="0"+recordHours+":"+"0"+recordMinutes+":"+"0"+recordSeconds;
                            if(recordMinutes>=60){
                                ++recordHours;
                                recordMinutes=0;
                                handleRecordText_Notification="0"+recordHours+":"+"0"+recordMinutes+":"+"0"+recordSeconds;

                            }
                        }



                        //UI Check
                        if(recordSeconds > 9){
                            recordSecondsString=String.valueOf(recordSeconds);

                        }else{
                            recordSecondsString=String.valueOf("0"+recordSeconds);
                        }


                        if(recordMinutes > 9){
                            recordMinutesString=String.valueOf(recordMinutes);

                        }else{
                            recordMinutesString=String.valueOf("0"+recordMinutes);
                        }

                        if(recordHours > 9){
                            recordHoursString=String.valueOf(recordHours);

                        }else{
                            recordHoursString=String.valueOf("0"+recordHours);

                        }





                        handleRecordText_Notification = recordHoursString + ":" + recordMinutesString + ":" + recordSecondsString;
                        record_duration_txt.setText(handleRecordText_Notification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handlerForRecordDuration.postDelayed(this, 1000);
            }

        });
    }

    public void DefineExistingRecording(String recordName) {
        playRecord = new MediaPlayer();
        try {

            playRecord.setDataSource(recordName);
            playRecord.prepare();
            Toast.makeText(getApplicationContext(), "Record Found For this Note", Toast.LENGTH_LONG).show();
            PAUSE_VALUE = 1;

        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "No Recordings for this Note,Yet", Toast.LENGTH_LONG).show();
        }
    }


    public void AccessWritePermssions() {
        //Make New directory
        File companyFile = new File(getExternalStorageDirectory() + "/" + "SPRecordings");
        companyFile.mkdir();
    }


    //FAB buttons Action Listener

    public void RecordBtn(View v) {
        try {
            recordStop = findViewById(R.id.Recordbtn);
            recordingNow=true;
            switch (VALUE_OF_RECORD) {
                case 1:
                    AccessMicPermissions();
                    DialogBoxWithRecordSave();
                    hideMediaComponents();
                    VALUE_OF_RECORD = 2;
                    text = "Stop";
                    break;

                case 2:
                    stopRecording();
                    RefreshRecordingValues();
                    handlerForRecordDuration.removeCallbacksAndMessages(null);
                    hideRecordComponent();
                    VALUE_OF_RECORD = 1;
                    text = "Record";
                    recordStop.setLabelText(text);
                    nm.cancelAll();
                    break;

            }

        } catch (Exception error) {
            error.printStackTrace();
            Snackbar.make(v, "Error Recording Lecture Check MIC Permissions", Snackbar.LENGTH_LONG).show();

        }
    }

    public void hideRecordComponent(){
        if(record_duration_txt.getVisibility()==View.VISIBLE){
            record_duration_txt.setVisibility(View.INVISIBLE);
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
            record_duration_txt.startAnimation(anim);
        }

    }
    public void showRecordComponent(){
        if(record_duration_txt.getVisibility()==View.INVISIBLE){
            record_duration_txt.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            record_duration_txt.startAnimation(anim);
        }
    }

    public void playRecordPauseBtn(View v) {
        try {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    4);
            playRecordingOrPause(v);

        } catch (Error e) {
            e.printStackTrace();
            Snackbar.make(v, "No Record For this Note Yet ", Snackbar.LENGTH_LONG).show();
        }
    }


    public void PauseRecordBtn(View view) {
        pasueRecord = findViewById(R.id.PauseRecord);
        try {

            if(record != null){
                switch (PAUSE_VALUE) {
                    case 1:
                        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
                            record.pause();
                        }
                        pasueRecord.setLabelText("Resume");
                        Snackbar.make(view, "Pause", Snackbar.LENGTH_LONG).show();

                        handlerForRecordDuration.removeCallbacksAndMessages(null);

                        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.play_ico);
                        pasueRecord.setImageDrawable(img);

                        PAUSE_VALUE = 2;
                        break;
                    case 2:
                        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
                            try {
                                record.resume();
                            } catch (Exception e) {
                                e.printStackTrace();
                                PAUSE_VALUE = 2;
                            }
                        }
                        pasueRecord.setLabelText("Pause");
                        Snackbar.make(view, "Resuming", Snackbar.LENGTH_LONG).show();

                        Drawable imgPause = getApplicationContext().getResources().getDrawable(R.drawable.pause_ico);
                        pasueRecord.setImageDrawable(imgPause);
                        PAUSE_VALUE = 1;

                        record_duration_txt.setText(handleRecordText_Notification);
                        Handle_Record_Duration();
                        break;

                }
            }else{
                Snackbar.make(view, "No Recordings Running", Snackbar.LENGTH_LONG).show();

            }

        } catch (Exception error) {
            Snackbar.make(view, "No Recordings Running", Snackbar.LENGTH_LONG).show();
            PAUSE_VALUE = 2;
            error.printStackTrace();
        }
    }


    public void savebtnAction(View view) {
        DialogBoxWithoutRecordSave();
        refer = 0;
    }


    public void TxtToSpeechbtnAction(View v) {
        try {
            speakBtn = findViewById(R.id.speakBtn);
            switch (SPEAK_STATE) {
                case 1:
                    ttsEngine = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if ( status == TextToSpeech.SUCCESS ){
                                int result = ttsEngine.setLanguage(Locale.UK);
                                if ( result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED ){
                                    Log.e("TTS", "Bad Format");
                                } else {
                                    SpeakOut();
                                }
                            }
                        }
                    });
                    speakBtn.setLabelText("Stop Speech");
                    SPEAK_STATE = 2;
                    break;
                case 2:
                    ttsEngine.shutdown();
                    ttsEngine.stop();
                    speakBtn.setLabelText("Speak Note");
                    SPEAK_STATE = 1;
                    break;
            }
        } catch (Exception error) {
            ttsEngine.stop();
            ttsEngine.shutdown();
        }
    }



    public void shareActionBtn(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose what to share :")
                // An OK button that does nothing
                .setPositiveButton(" TXT Note", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        shareNoteTXT();
                    }
                })
                .setNegativeButton("Recorded Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (okPressed==false){
                            shareRecord(recordName);
                        } else if (okPressed==true){
                            shareRecord(pathForRecordings);

                        }

                    }})
                .setCancelable(true);
        AlertDialog d2 = builder.create();
        d2.show();

    }


    public void shareRecord(String file) {
        try {
            //Sharing the recorded Note
            Intent shareIntentRecord = new Intent(Intent.ACTION_SEND);
            shareIntentRecord.setType("audio/mp3");
            shareIntentRecord.putExtra(Intent.EXTRA_STREAM, Uri.parse(file));
            shareIntentRecord.putExtra(Intent.EXTRA_SUBJECT, "Share Record");
            shareIntentRecord.putExtra(Intent.EXTRA_TEXT, ed.getText().toString());
            startActivity(Intent.createChooser(shareIntentRecord, "Share Record"));
        } catch (NullPointerException error) {
            Toast.makeText(getApplicationContext(), "No Record To Declare", Toast.LENGTH_LONG).show();
            error.printStackTrace();
        } catch (IllegalStateException error2) {
            Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
            error2.printStackTrace();
        }
    }

    public void shareNoteTXT() {

        //SHaring the Note Text>>>>
        Intent shareIntentNote = new Intent(Intent.ACTION_SEND);
        shareIntentNote.setType("text/plain");
        shareIntentNote.putExtra(Intent.EXTRA_SUBJECT, "Share Note");
        shareIntentNote.putExtra(Intent.EXTRA_TEXT, ed.getText().toString());
        startActivity(Intent.createChooser(shareIntentNote, "Share File"));


    }

    public void SpeakOut() {
        String TextToSpeak = ed.getText().toString();

        ttsEngine.speak(TextToSpeak, TextToSpeech.QUEUE_FLUSH, null);


    }

    public void onDestroy() {
        super.onDestroy();
        try {
            ttsEngine.stop();
            ttsEngine.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void update() {

        EditPaneActivity.this.runOnUiThread(new Runnable() {

            public void run() {
                if ( playRecord != null ){
                    try {
                        MediaSeekbar.setMax(playRecord.getDuration() / 1000);
                        int currentPosition = playRecord.getCurrentPosition() / 1000;
                        MediaSeekbar.setProgress(currentPosition);


                        //Add SeekBar Listener
                        MediaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if ( playRecord != null && fromUser == true ){
                                    playRecord.seekTo(progress * 1000);
                                    getCurrentProgressOfPlayMedia();
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        getCurrentProgressOfPlayMedia();
                    } catch (Exception e) {
                        hideMediaComponents();
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, 1000);
            }

        });
    }


    public void showMediaComponents() {
        //to refresh the duration value

        if ( playRecord != null ){
            Duration.setVisibility(View.VISIBLE);

            CurrentPositionOfMedia.setVisibility(View.VISIBLE);
            if(okPressed==false){
                nm.cancelAll();
                Notify("Playing Record", recordName, R.drawable.ic_launcher_foreground, "ID3030", 3030, getPackageName());
            }else if(okPressed==true){
                nm.cancelAll();
                Notify("Playing Record", pathForRecordings, R.drawable.ic_launcher_foreground, "ID3030", 3030, getPackageName());
            }

            getDurationOfPlayMedia_ForShowMediaUpdate();
            if ( MediaSeekbar.getVisibility() == View.INVISIBLE ){
                MediaSeekbar.setVisibility(View.VISIBLE);

                //use animation w/ seekbar
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
                MediaSeekbar.startAnimation(anim);
            }
        }
    }


    public void getDurationOfPlayMedia_ForShowMediaUpdate() {

        String out = "";
        long dur = playRecord.getDuration();
        String seconds = String.valueOf((dur % 60000) / 1000);

        String minutes = String.valueOf(dur / 60000);
        out = minutes + ":" + seconds;
        if ( seconds.length() == 1 ){
            Duration.setText("0" + minutes + ":0" + seconds);
        } else {
            Duration.setText("0" + minutes + ":" + seconds);
        }

    }


    public void hideMediaComponents() {
        try {
            MediaSeekbar = findViewById(R.id.MediaSeekBar);
            Duration = findViewById(R.id.Duration);
            CurrentPositionOfMedia = findViewById(R.id.CurrentProgress);

            MediaSeekbar.setVisibility(View.INVISIBLE);
            Duration.setVisibility(View.INVISIBLE);
            CurrentPositionOfMedia.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    public void searchWordBtn(View v){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.search_aword, null);
        final EditText searchaWord=layout.findViewById(R.id.searchaWord);
        Button searchwordBtn = layout.findViewById(R.id.searchWordDialogBox);
        Button cancelbtn = layout.findViewById(R.id.cancelWordDialogBox);
        builder.setView(layout);


        searchwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    d.dismiss();

                    final ProgressDialog progress= ProgressDialog.show(v.getContext(),"Wait..","Searching Word");



                    CountDownTimer ctd=new CountDownTimer(2000,1200) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            String value=ed.getText().toString();

                            String newText=searchaWord.getText().toString();
                            String valueDeplete = value.replaceAll(newText, "<span style=\"background-color:"+spancolor+"\">" + "<font color="+"'"+textcolor+"'"+">"+newText+"</font>"+ "</span>");
                            ed.setText(Html.fromHtml(valueDeplete));
                                progress.dismiss();
                        }
                    }.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });



        builder.setCancelable(true);
        d = builder.create();
        d.getWindow().setGravity(Gravity.BOTTOM);
        d.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation1;


        d.show();


    }

    public void Listener_For_TextColor(View view){
            textcolor=view.getTag().toString();
    }

    public void Listener_For_SpanColor(View view){
            spancolor=view.getTag().toString();
    }


    //separate Record Eraser
    public void Delete_Record(View view){
            try {
                String pathForRecordings = getExternalStorageDirectory() + "/SPRecordings/" + fileName
                        + ".mp3";
                File recordFile = new File(pathForRecordings);
                recordFile.delete();
                Snackbar.make(view,fileName+" Record is deleted Successfully",Snackbar.LENGTH_SHORT).show();
            }catch (Exception error){
                error.printStackTrace();
            }
    }


}