package com.scrappers.notepadpp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;
import com.baoyz.widget.PullRefreshLayout;
import org.jetbrains.annotations.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import static android.os.Environment.getExternalStorageDirectory;

interface Adapter_Related{

    void Refresh();
    void defineAdapter(ArrayList<String> mainText,ArrayList<String> subText);
    void addnewNote(View view);
    void ChangeGridBtn(View view);
    void ChangeColumnsViewData();
    void CheckColumnsViewData();
}

interface Search_View_Related{
    void SearchView();
}

interface Three_Dot_Btn_Related{
    void settingsAlert();
    void setting(View view);
}

interface Stoarge_Related{
    void readTextFiles(String path);
    void makeTheExpansionDir(String path);
    void readTxtFiles(String path);
    void WriteExternalStoargePermission();
    void writeNumOfColumns(int n);
    void readNumOfColumns();
}


public class MainActivity extends AppCompatActivity implements Adapter_Related,
                                                                    Stoarge_Related,
                                                                    Search_View_Related,
                                                                    Three_Dot_Btn_Related

{

    //Attributes/Fields/Vars/Objects
    CustomListAdapter lsAdapter;
    ArrayList<String> mainTitle = new ArrayList<>();
    ArrayList<String> subTitle = new ArrayList<>();
    GridView lsview;
    static int refer = 0;
    static String fileName;
    static String recordName;
    static SearchView srch;
    int numOfColumns = 0;
    ArrayAdapter<String> adapter;
    AlertDialog alrt;
    static String finalOutText;
    public static int importPosition=0;



    //Methods/Functions


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //accessing permissions

        WriteExternalStoargePermission();
//        WriteExternalStoargePermission();

//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
//            startService(new Intent(getApplicationContext(), BackgroundService.class));
//        }

        //check files in the file systems /Storage/emulated/0/Android/com.Expand/
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
            readTxtFiles(getApplicationContext().getFilesDir() + "/");
//            System.out.print(getApplicationContext().getDataDir()+"/");
        }

        defineAdapter(mainTitle, subTitle);
        CheckColumnsViewData();
        SearchView();
        if ( Intent.ACTION_VIEW.equals(getIntent().getAction()) ){
            File file = new File(getIntent().getData().getPath());
        }

    }


    public void SearchView() {
        //Handling SearchView And SearchView Animation & Listeners
        srch = findViewById(R.id.search_bar);


        srch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                lsAdapter.clear();
                //use animation w/ searchBar SearchView
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_up);
                srch.startAnimation(anim);
//                circleReveal(R.id.search_bar,1,true,true);

            }
        });


        //SearchView onClose Listener
        srch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                srch.startAnimation(anim);

                //Animation Listener w/ overridable methods
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
//                circleReveal(R.id.search_bar,1,true,false);
                return true;
            }
        });


        srch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lsview.setAdapter(adapter);

                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }





    public void WriteExternalStoargePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);


    }

    public void Refresh(){
        final PullRefreshLayout refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsAdapter.notifyDataSetInvalidated();
                lsAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);

            }
        });

    }


    public void defineAdapter(ArrayList<String> mainText, final ArrayList<String> subText) {

        lsAdapter = new CustomListAdapter(this, mainTitle,subTitle);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,mainTitle);
        lsview = findViewById(R.id.listFiles);
        lsview.setAdapter(lsAdapter);

        Refresh();

        lsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(getApplicationContext(), EditPaneActivity.class));
                finish();
                fileName = lsAdapter.getItem(position);

                try {
                    readTextFiles(getApplicationContext().getFilesDir() + "/" + fileName);
                    recordName = getExternalStorageDirectory() + "/SPRecordings/" + fileName
                            + ".mp3";
                    refer = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        lsview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                builder.setMessage("Are you Sure you want to delete this item ?")
                        // An OK button that does nothing
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //For Note Files
                                File file = new File(getApplicationContext().getFilesDir() + "/" + lsAdapter.getItem(position));
                                file.delete();

                                //For Recordings
                                String pathForRecordings = getExternalStorageDirectory() + "/SPRecordings/" + lsAdapter.getItem(position)
                                        + ".mp3";
                                File recordFile = new File(pathForRecordings);
                                recordFile.delete();


                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false);
                AlertDialog d2 = builder.create();

                d2.show();
                return true;


            }
        });


    }



    public void readTextFiles(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            //to avoid showing null results w/ attempt to open the item
            if ( reader.ready() == false ){
                finalOutText = "";
                reader.close();
            } else {

                String outputText = reader.readLine() + "\n";

                while (reader.ready()) {
                    outputText += reader.readLine() + "\n";
                }
                finalOutText = outputText;
                reader.close();
                Toast.makeText(getApplicationContext(), "Read Successful", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeTheExpansionDir(String path) {
        File directory = new File(Environment.getExternalStorageDirectory() + path);
        directory.mkdirs();
    }

    public void readTxtFiles(String path) {
        try {
//            makeTheExpansionDir(path);
            makeTheExpansionDir(path);
            //declaring a fie instance w/ a specific read/write  path
            File files[] = new File(path).listFiles();
            //using for each to list all files
            for (int num = 0; num <= files.length - 1; ++num) {
                mainTitle.add(files[num].getName());

                Date d = new Date(files[num].lastModified());
                subTitle.add(String.valueOf(d.toLocaleString()));

            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void addnewNote(View view) {


        startActivity(new Intent(getApplicationContext(), EditPaneActivity.class));
        finish();
        refer = 0;
    }



    public void settingsAlert() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.alrtbox_items_menu, null);
        builder.setView(layout);


        Button aboutbtn = (Button) layout.findViewById(R.id.about);
        aboutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), aboutActivity.class));

            }
        });
        Button thymesbtn = (Button) layout.findViewById(R.id.Thymes);
        thymesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Coming in next Updates with \n Lovely Thymes :)", Toast.LENGTH_SHORT).show();
            }
        });

        alrt = builder.create();
        alrt.getWindow().setGravity(Gravity.BOTTOM);
        alrt.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;

        alrt.show();
    }

    public void setting(View view) {
        settingsAlert();
    }


    public void ChangeGridBtn(View view) {

        ChangeColumnsViewData();

    }


    //Handling GridView & Number of Columns
    public void writeNumOfColumns(int data) {
        try {
            BufferedWriter Bwriter = new BufferedWriter(new FileWriter(getExternalStorageDirectory() + "/SPRecordings/" +
                    "column"));
            Bwriter.write(String.valueOf(data));
            Bwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readNumOfColumns() {
        try {
            BufferedReader Breader = new BufferedReader(new FileReader(getExternalStorageDirectory() + "/SPRecordings/" +
                    "column"));
            String numString = Breader.readLine();
            numOfColumns = Integer.parseInt(numString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ChangeColumnsViewData() {
        readNumOfColumns();
        if ( lsview.getNumColumns() == 1 ){
            lsview.setNumColumns(2);
            writeNumOfColumns(2);

        } else if ( lsview.getNumColumns() == 2 ){
            lsview.setNumColumns(1);
            writeNumOfColumns(1);
        }
    }

    public void CheckColumnsViewData() {
        try {
            readNumOfColumns();
            lsview.setNumColumns(numOfColumns);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }