package com.scrappers.notepadpp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;



interface aboutActivityUI{
    void shareMyApp(View view);
    void animateLogoAndBtns( Animation animation );
    void runSocials(String socialLink);
    void thomasFacebook(View view);
    void pavlyFacebook(View view);
    void Gmail(View view);
}

public class aboutActivity extends AppCompatActivity implements aboutActivityUI{
    //Attributes
    Uri u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Call request Portrait Orientation
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        animateLogoAndBtns(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open));
//        loadGAds();
    }
//    public void loadGAds(){
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//    }

    //Web Btn Listener
//    public void gotowebListener(View view ){
//        //Create a new Intent to open browser w/ specified http URI
////
////        Intent intent=new Intent(Intent.ACTION_VIEW,  u.parse("https://www.facebook.com/groups/353540175525706/"));
////        //Start the activity
//            startActivity(new Intent(this,webActivity.class));
//    }
    public void shareMyApp(View view){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=com.Scrappers.cairobus";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Please Support US ! By Scrappers >>>>"));
    }
    public void animateLogoAndBtns( Animation animation ){
        CardView btnshare=findViewById(R.id.cardView);
        CardView gotoweb=findViewById(R.id.pav);

        btnshare.startAnimation(animation);
        gotoweb.startAnimation(animation);


    }
    public void runSocials(String socialLink){
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(socialLink)));
    }
    public void thomasFacebook(View view){
        runSocials("https://www.facebook.com/2Math.toto");
    }
    public void Gmail(View view){
        runSocials("https://mail.google.com/mail/mu/mp/840/#tl/priority/%5Esmartlabel_personal");
    }
    public void pavlyFacebook(View view){
        runSocials("https://www.facebook.com/profile.php?id=100010116038073");
    }


}
