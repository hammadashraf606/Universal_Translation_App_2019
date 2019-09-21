package com.example.mziccard.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener, NumberPicker.OnValueChangeListener,InternetConnectivityListener {

    // todo API_KEY should not be stored in plain sight
    public static final int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public static boolean active = false;
    public static AppCompatActivity mainActivity;

    private static final String API_KEY = "AIzaSyDZJtBthHsstFZA0omlA-vjNoln-w2H1Ic";
    private static final int REQ_CODE_SPEECH_INPUT = 1;
    private static final String LOG_TAG = MainActivity.class.getName();
    HashMap<String, String> map = new HashMap<>();
    String TargetLang = "en";
    static List<String> languageNames = new ArrayList<String>();
    public EditText sample;
    DrawerLayout drawerLayout;
    Button btn;
    static ArrayAdapter<String> langAdapter;
    static ArrayAdapter<String> langAdapterDark;
    static Boolean darkthemeflag = false;
    LinearLayout linearLayout, srclayout, targetlayout,mainlayout;
    static ArrayList<String> languageCodes = new ArrayList<String>();
    final Handler textViewHandler = new Handler();
    public TextView textView;
    static SearchableSpinner language;
    static SearchableSpinner srclang;
    static ProgressDialog progressBar;
    CircleImageView speakButton, TTSButton, camButton, copyButton, clearButton, pasteButton;
    private Dialog process_tts;                             //    Dialog box for Text to Speech Engine Language Switch
    private TextToSpeech mTextToSpeech;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    GetLanguages getLanguages;
    View navheader;
    AdView adView;

    InterstitialAd interstitialAd;
    InternetAvailabilityChecker mInternetAvailabilityChecker;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exit();
        }
    }

    public void exit() {

        // Build an AlertDialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button positive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button neutral = dialogView.findViewById(R.id.exit);


        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            //    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

            }
        });

        //Neutral Button
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInternetAvailabilityChecker
                        .removeInternetConnectivityChangeListener(MainActivity.this);
                active = false;
                finishAffinity();
                moveTaskToBack(true);

            }
        });

        // Display the custom alert dialog on interface
/*

      */
/*  final AdView ad = (AdView) dialogView.findViewById(R.id.larban);
      *//*
  AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
                ad.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
//                super.onAdLoaded();
                ad.setVisibility(View.VISIBLE);
            }
        });
        ad.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                AdRequest adRequest = new AdRequest.Builder().build();
                ad.setVisibility(View.VISIBLE);
                ad.loadAd(adRequest);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                ad.setVisibility(View.GONE);
            }

        });

*/

        dialog.show();

    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InternetAvailabilityChecker.init(getApplicationContext());
        Banner();
        pasteButton = (CircleImageView) findViewById(R.id.btnPaste);
        camButton = (CircleImageView) findViewById(R.id.btnVision);
        drawerLayout = (DrawerLayout) findViewById(R.id.navDrawer);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainlayout=(LinearLayout)findViewById(R.id.mainlayout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        srclang = (SearchableSpinner) findViewById(R.id.spnSrc);
        clearButton = (CircleImageView) findViewById(R.id.btnClr);
        srclayout = (LinearLayout) findViewById(R.id.LLsrc);
        targetlayout = (LinearLayout) findViewById(R.id.LLtarget);
        textView = (TextView) findViewById(R.id.text_view);
        language = (SearchableSpinner) findViewById(R.id.spnTarget);
        copyButton = (CircleImageView) findViewById(R.id.btnCopy);
        sample = (EditText) findViewById(R.id.etsample);
        sample.setHintTextColor(getResources().getColor(android.R.color.white));
        int textSize=SaveSharedPreference.getPrefTextSize(MainActivity.this);
        sample.setTextSize(((float) textSize));
        mainActivity=MainActivity.this;
        btn = (Button) findViewById(R.id.btnTranslate);
        textView.setTextSize(((float) textSize));
        linearLayout = (LinearLayout) findViewById(R.id.activity_main);
        speakButton = (CircleImageView) findViewById(R.id.btnSpeak);
        TTSButton = (CircleImageView) findViewById(R.id.btnSTT);
        //navigationView.addHeaderView(navheader);
        navheader=navigationView.getHeaderView(0);
        Menu navmenu= navigationView.getMenu();
        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading Languages");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getLanguages = new GetLanguages();
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        try {
            mInternetAvailabilityChecker.addInternetConnectivityListener(MainActivity.this);
        }catch (NullPointerException e)
        {
            //Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(SaveSharedPreference.getThemeType(this).equals("DARK"))
        {
            mainlayout.setBackgroundColor(getResources().getColor(R.color.darkbg));
            linearLayout.setBackgroundColor(getResources().getColor(R.color.darkbg));
            srclayout.setBackgroundResource(R.drawable.roundeddark);
            targetlayout.setBackgroundResource(R.drawable.roundeddark);
            sample.setBackgroundResource(R.drawable.roundeddark);
            sample.setTextColor(getResources().getColor(android.R.color.white));
            textView.setBackgroundResource(R.drawable.roundeddark);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            speakButton.setColorFilter(getResources().getColor(android.R.color.white));
            camButton.setColorFilter(getResources().getColor(android.R.color.white));
            copyButton.setColorFilter(getResources().getColor(android.R.color.white));
            clearButton.setColorFilter(getResources().getColor(android.R.color.white));
            pasteButton.setColorFilter(getResources().getColor(android.R.color.white));
            TTSButton.setColorFilter(getResources().getColor(android.R.color.white));
            srclang.setBackgroundResource(R.drawable.customspinnerdark);
            language.setBackgroundResource(R.drawable.customspinnerdark);
            btn.setTextColor(getResources().getColor(android.R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
            btn.setBackground(getResources().getDrawable(R.drawable.roundeddark));
            SaveSharedPreference.setThemeType(MainActivity.this,"DARK");
            navigationView.getMenu().findItem(R.id.nav_Theme).setTitle("Switch  to Day Mode");
            drawerLayout.setBackgroundColor(getResources().getColor(R.color.darkbg));
            //navigationView.setBackgroundColor(getResources().getColor(R.color.darkbg));
            navheader.setBackgroundColor(getResources().getColor(R.color.darkbg));
            darkthemeflag = true;

        }



        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial));
        requestNewInterstitial();
        if (!isOnline()) {
            if(progressBar.isShowing())
            {
                progressBar.dismiss();
            }
            Toasty.error(MainActivity.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toasty.success(MainActivity.this, "Internet Connected!", Toast.LENGTH_SHORT, true).show();
        }




        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(false);
                        menuItem.setCheckable(false);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        if(menuItem.getItemId() == R.id.nav_rate)
                        {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

                        }
                        if (menuItem.getItemId() == R.id.nav_TextSize) {
                            showNumberPicker();
                        }
                        if (menuItem.getItemId() == R.id.nav_Theme) {
                            if (!darkthemeflag) {
                                mainlayout.setBackgroundColor(getResources().getColor(R.color.darkbg));
                                linearLayout.setBackgroundColor(getResources().getColor(R.color.darkbg));
                                srclayout.setBackgroundResource(R.drawable.roundeddark);
                                targetlayout.setBackgroundResource(R.drawable.roundeddark);
                                sample.setBackgroundResource(R.drawable.roundeddark);
                                sample.setTextColor(getResources().getColor(android.R.color.white));
                                textView.setBackgroundResource(R.drawable.roundeddark);
                                textView.setTextColor(getResources().getColor(android.R.color.white));
                                speakButton.setColorFilter(getResources().getColor(android.R.color.white));
                                camButton.setColorFilter(getResources().getColor(android.R.color.white));
                                copyButton.setColorFilter(getResources().getColor(android.R.color.white));
                                clearButton.setColorFilter(getResources().getColor(android.R.color.white));
                                pasteButton.setColorFilter(getResources().getColor(android.R.color.white));
                                TTSButton.setColorFilter(getResources().getColor(android.R.color.white));
                                srclang.setBackgroundResource(R.drawable.customspinnerdark);
                                language.setBackgroundResource(R.drawable.customspinnerdark);
                                int srcindex=srclang.getSelectedItemPosition();
                                int langindex=language.getSelectedItemPosition();
                                srclang.setAdapter(langAdapterDark);
                                language.setAdapter(langAdapterDark);
                                srclang.setSelection(srcindex);
                                language.setSelection(langindex);
                                btn.setTextColor(getResources().getColor(android.R.color.white));
                                toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
                                btn.setBackground(getResources().getDrawable(R.drawable.roundeddark));
                              SaveSharedPreference.setThemeType(MainActivity.this,"DARK");
                                menuItem.setTitle("Switch to Day Mode");
                                //navigationView.setBackgroundColor(getResources().getColor(android.R.color.black));
                                navheader.setBackgroundColor(getResources().getColor(R.color.darkbg));
                                darkthemeflag = true;
                                //showNumberPicker();
                            } else if (darkthemeflag) {
                                mainlayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                                linearLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                                sample.setTextColor(getResources().getColor(android.R.color.black));
                                textView.setBackgroundResource(R.drawable.roundedwhite);
                                srclayout.setBackgroundResource(R.drawable.roundedwhite);
                                targetlayout.setBackgroundResource(R.drawable.roundedwhite);
                                sample.setBackgroundResource(R.drawable.roundedwhite);
                                textView.setTextColor(getResources().getColor(android.R.color.black));
                                srclang.setBackgroundResource(R.drawable.customspinner);
                                language.setBackgroundResource(R.drawable.customspinner);
                                int srcindex=srclang.getSelectedItemPosition();
                                int langindex=language.getSelectedItemPosition();
                                srclang.setAdapter(langAdapter);
                                language.setAdapter(langAdapter);
                                srclang.setSelection(srcindex);
                                language.setSelection(langindex);
                                speakButton.setColorFilter(getResources().getColor(R.color.imgassetcolor));
                                camButton.setColorFilter(getResources().getColor(R.color.imgassetcolor));
                                copyButton.setColorFilter(getResources().getColor(R.color.imgassetcolor));
                                clearButton.setColorFilter(getResources().getColor(R.color.imgassetcolor));
                                pasteButton.setColorFilter(getResources().getColor(R.color.imgassetcolor));
                                TTSButton.setColorFilter(getResources().getColor(R.color.imgassetcolor));
                                navheader.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                                btn.setBackground(getResources().getDrawable(R.drawable.rounded));
                                btn.setTextColor(getResources().getColor(android.R.color.white));
                                toolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                                menuItem.setTitle("Switch to Night Mode");
                                SaveSharedPreference.setThemeType(MainActivity.this,"DAY");
                                darkthemeflag = false;
                                //showNumberPicker();
                            }

                        }
                        if(menuItem.getItemId()==R.id.nav_Head)
                        {
                            showBubble();
                            //startService(new Intent(MainActivity.this, ChatHeadService.class));
                        }
                        if (menuItem.getItemId() == R.id.nav_share) {
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            } else {

                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");

                                String shareBodyText = "Rate this app \n \"https://play.google.com/store/apps/details?id=" + getPackageName();
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                startActivity(Intent.createChooser(sharingIntent, "Sharing Option"));
                            }
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


        language.setTitle("Select Target Language");
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                          int index = language.getSelectedItemPosition();
                        String langcode = languageCodes.get(index);
                        TranslateOptions options = TranslateOptions.newBuilder()
                                .setApiKey(API_KEY)
                                .build();
                        Translate translate = options.getService();
                        final Translation translation =
                                translate.translate(sample.getText().toString(),
                                        Translate.TranslateOption.targetLanguage(langcode));
                        textViewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (textView != null) {
                                    textView.setText(translation.getTranslatedText());
                                }
                            }
                        });
                        return null;
                    }
                }.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        progressBar.setProgress(0);//initially progress is 0
//        progressBar.setMax(100);//sets the maximum value 100

        srclang.setTitle("Select Source Language");
        if (isOnline()) {
//
            try {
                progressBar.show();
                getLanguages.execute();
               // progressBar.dismiss();
            }catch (Exception e)
            {

                Toasty.error(mainActivity, e.getMessage(), Toast.LENGTH_SHORT, true).show();

            }
            try {
                mTextToSpeech = new TextToSpeech(getApplicationContext(), this);
            }catch (Exception e)
            {

                Toasty.error(mainActivity, e.getMessage(), Toast.LENGTH_SHORT, true).show();
            }

        }
        else
        {
            if(progressBar.isShowing())
            {
                progressBar.dismiss();
            }
            if(mTextToSpeech!=null) {
                mTextToSpeech.shutdown();
            }
            else {

                Toasty.error(MainActivity.this, "TTS INITIALIZATION FAILED", Toast.LENGTH_SHORT, true).show();

            }
            if(getLanguages.getStatus()==AsyncTask.Status.RUNNING)
            {getLanguages.cancel(true);}
            Toasty.error(MainActivity.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();
        }



        process_tts = new Dialog(MainActivity.this);
        process_tts.setContentView(R.layout.dialog_processing_tts);
        process_tts.setTitle(getString(R.string.process_tts));



        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {

                    Intent intent = new Intent(MainActivity.this, cameramain.class);
                    startActivity(intent);
                }
            }
        });


        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    String pasteData = "";

                    // If it does contain data, decide if you can handle the data.
                    assert clipboard != null;
                    if (!(clipboard.hasPrimaryClip())) {
                        Toasty.info(MainActivity.this, "Nothing to paste", Toast.LENGTH_SHORT, true).show();

                    } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

                        Toasty.info(MainActivity.this, "Copied data is not text", Toast.LENGTH_SHORT, true).show();

                        // since the clipboard has data but it is not plain text

                    } else {

                        //since the clipboard contains plain text.
                        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                        // Gets the clipboard as text.
                        pasteData = item.getText().toString();
                        sample.setText(pasteData);
                    }
                }
            }
        });


        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else

                {
                    String text = textView.getText().toString();
                    if (text.isEmpty()) {
                        Toasty.info(MainActivity.this, "No text to copy", Toast.LENGTH_SHORT, true).show();

                    } else {
                        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        myClip = ClipData.newPlainText("text", text);
                        myClipboard.setPrimaryClip(myClip);
                        Toasty.success(MainActivity.this, "Text Copied", Toast.LENGTH_SHORT, true).show();


                    }
                }
            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    textView.setText("");
                    sample.setText("");
                    Toasty.info(MainActivity.this,"Text Boxes Cleared!",Toast.LENGTH_LONG,true).show();
                }
            }
        });


        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    if (srclang.getVisibility() == View.GONE) {
                        srclang.setVisibility(View.VISIBLE);
                        Toasty.info(MainActivity.this, "Select Speech Language & Press the  Button Again", Toast.LENGTH_SHORT, true).show();


                    } else {
                        int index = srclang.getSelectedItemPosition();
                        String langcode = languageCodes.get(index);
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langcode);
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
                        try {
                            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                        } catch (ActivityNotFoundException anf) {
                            Toasty.error(MainActivity.this, getString(R.string.language_not_supported), Toast.LENGTH_SHORT, true).show();

                        }
                    }

                }
            }
        });


        TTSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                else {
                    if(textView.getText().toString().isEmpty())
                    {
                        Toasty.error(MainActivity.this, "No text to speak", Toast.LENGTH_SHORT, true).show();

                    }
                    else{speakOut();}

                }

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {

                    if (sample.getText().toString().isEmpty()) {
                        Toasty.error(MainActivity.this, "Enter Text", Toast.LENGTH_SHORT, true).show();

                    } else {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                int index = language.getSelectedItemPosition();
                                String langcode = languageCodes.get(index);
                                TranslateOptions options = TranslateOptions.newBuilder()
                                        .setApiKey(API_KEY)
                                        .build();
                                Translate translate = options.getService();
                                final Translation translation =
                                        translate.translate(sample.getText().toString(),
                                                Translate.TranslateOption.targetLanguage(langcode));
                                textViewHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (textView != null) {
                                            textView.setText(translation.getTranslatedText());
                                        }
                                    }
                                });
                                return null;
                            }
                        }.execute();
                    }

                }
            }
        });

    }

    private void showBubble() {
        if(Utils.canDrawOverlays(MainActivity.this))
            showChatHead();
        else{
            requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
        }
    }
    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    private void showChatHead() {
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            startService(new Intent(MainActivity.this, ChatHeadService23.class));
            finish();
            //System.exit(0);
        }
        else {
            startService(new Intent(MainActivity.this, ChatHeadService.class));
            //mainActivity.setVisible(false);
            finish();
            //System.exit(0);
        }
    }

    private void Banner() {

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                adView.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onInit(int status) {
        Log.e("Inside----->", "onInit, status="+status);
        if (status == TextToSpeech.SUCCESS && isOnline()) {
            int index = language.getSelectedItemPosition();
            String langcode = languageCodes.get(index);
            int result = mTextToSpeech.setLanguage(new Locale(langcode));
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Toasty.error(MainActivity.this, "Language Pack Missing", Toast.LENGTH_SHORT, true).show();

            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toasty.error(MainActivity.this, getString(R.string.language_not_supported), Toast.LENGTH_SHORT, true).show();

            }
            TTSButton.setEnabled(true);
            mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.e("Inside", "OnStart");
                    process_tts.hide();
                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        } else {
            Log.e(LOG_TAG, "TTS Initilization Failed");
            if (progressBar.isShowing())
            {
                progressBar.dismiss();
            }
            mTextToSpeech.stop();
            Toasty.error(MainActivity.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
//try {
    super.onResume();

        if (isOnline() && getLanguages.getStatus() == AsyncTask.Status.FINISHED || getLanguages.getStatus() == AsyncTask.Status.RUNNING) {
            active = true;
            super.onResume();
            //mTextToSpeech= new TextToSpeech(MainActivity.this,this);

        }
        else if(!isOnline())
        {
            Toasty.error(mainActivity, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();

            active=true;
            super.onResume();
        }
        else {
            progressBar.show();
            getLanguages.execute();
            active = true;
            super.onResume();
        }
    }

    //  TEXT TO SPEECH ACTION
    @SuppressWarnings("deprecation")
    private void speakOut() {
        String lang = language.getSelectedItem().toString();
        int index = language.getSelectedItemPosition();
        String langcode = languageCodes.get(index);
//        Toast.makeText(this, lang, Toast.LENGTH_SHORT).show();
        int result = mTextToSpeech.setLanguage(new Locale(langcode));
        Log.e("Inside", "speakOut " + result);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Toasty.error(getApplicationContext(), "Sorry Language Pack Missing", Toast.LENGTH_SHORT, true).show();

            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toasty.error(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT, true).show();

        } else {
            String textMessage = textView.getText().toString();
            process_tts.show();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            mTextToSpeech.setSpeechRate(0.75f);
            mTextToSpeech.speak(textMessage, TextToSpeech.QUEUE_FLUSH, map);
            process_tts.dismiss();

        }
        //process_tts.hide();
    }

    //  RESULT OF SPEECH INPUT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    /*
                            Dialog box to show list of processed Speech to text results
                            User selects matching text to display in chat
                     */
                    final Dialog match_text_dialog = new Dialog(MainActivity.this);
                    match_text_dialog.setContentView(R.layout.dialog_matches_frag);
                    match_text_dialog.setTitle(getString(R.string.select_matching_text));
                    ListView textlist = (ListView) match_text_dialog.findViewById(R.id.list);
                    final ArrayList<String> matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matches_text);
                    textlist.setAdapter(adapter);
                    textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            sample.setText(matches_text.get(position));
                            match_text_dialog.dismiss();
                        }
                    });
                    match_text_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                            if (keyEvent.getKeyCode()==KeyEvent.KEYCODE_BACK)
                            {match_text_dialog.dismiss();}
                            return false;
                        }
                    });
                    match_text_dialog.show();
                    break;
                }
                break;
            }
            case OVERLAY_PERMISSION_REQ_CODE_CHATHEAD:{
                if (!Utils.canDrawOverlays(MainActivity.this)) {
                    needPermissionDialog(requestCode);
                    break;
                }else{
                    showChatHead();
                    break;
                }


            }
            default:
                return;
        }
    }

    private void needPermissionDialog(final int requestCode) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void requestNewInterstitial() {
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String item = adapterView.getItemAtPosition(i).toString();
//        String[] a= item.split("/");
//        Toast.makeText(MainActivity.this, item, Toast.LENGTH_SHORT).show();
//        TargetLang=a[1];


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //super.onResume();
        if (isConnected && getLanguages.getStatus() == AsyncTask.Status.FINISHED || getLanguages.getStatus() == AsyncTask.Status.RUNNING)
        {
            if(active)
            {
                Intent intent= new Intent(mainActivity,MainActivity.class);
                startActivity(intent);
                //finish();
            }
            else if (MyDialog.active)
            {
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    startService(new Intent(MainActivity.this, ChatHeadService23.class));
                    finish();
                    //System.exit(0);
                }
                else {
                    startService(new Intent(MainActivity.this, ChatHeadService.class));
                    //mainActivity.setVisible(false);
                    finish();
                    //System.exit(0);
                }
            }
//            try {
                //super.onResume();
//            }catch (Exception iae)
//            {
//                Log.e("ON RESUME EXCEPTION~~~",iae.getMessage());
//            }
            //onResume();
        }
        else if(isConnected &&(getLanguages.getStatus().equals(AsyncTask.Status.PENDING)))
        {
            getLanguages.execute();
        }
        else if(!isConnected)
        {
            if(progressBar.isShowing())
            {
                progressBar.dismiss();
            }
            if(mTextToSpeech!=null)
            {mTextToSpeech.shutdown();}
            Toasty.error(MainActivity.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    //  CHECK INTERNET CONNECTION

    private static class GetLanguages extends AsyncTask<Void, Void, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            final TranslateOptions options = TranslateOptions.newBuilder()
                    .setApiKey(API_KEY)
                    .build();
            Translate translate = options.getService();
            List<Language> languages = translate.listSupportedLanguages();
            ArrayList<String> languageNames = new ArrayList<String>();
            for (Language l : languages
                    ) {
                languageNames.add(l.getName());
                languageCodes.add(l.getCode());
                //            Toast.makeText(MainActivity.this, l.getName(), Toast.LENGTH_SHORT).show();

            }
            if (progressBar.isShowing())
            {
                progressBar.dismiss();
            }

            return languageNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            langAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_dropdown_item, result);
            langAdapterDark = new ArrayAdapter<String>(mainActivity, R.layout.spinnertextwhite, result);
            langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            langAdapterDark.setDropDownViewResource(R.layout.spinnertextwhite);
            if(darkthemeflag)
            {
                language.setAdapter(langAdapterDark);
                srclang.setAdapter(langAdapterDark);
            }
            else {
                language.setAdapter(langAdapter);
                srclang.setAdapter(langAdapter);
            }
            srclang.setSelection(21);
            language.setSelection(21);
            for (String s : languageNames
                    ) {
                Toasty.info(mainActivity, s, Toast.LENGTH_SHORT, true).show();

            }

            progressBar.dismiss();
        }

    }
    public boolean isOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            if (progressBar.isShowing())
//            {
//                progressBar.dismiss();
//            }
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
//        if (progressBar.isShowing())
//        {
//            progressBar.dismiss();
//        }
        return false;
    }

    public void showNumberPicker() {
        NumberPickerDialog newFragment = new NumberPickerDialog();
        newFragment.setValueChangeListener(this);
        newFragment.setMaxValue(90);
        newFragment.show(getSupportFragmentManager(), "time picker");

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

        //buffer.setText(String.valueOf(numberPicker.getValue()));
        sample.setTextSize(((float) i));
        textView.setTextSize(((float) i));
        SaveSharedPreference.setPrefTextSize(MainActivity.this,i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech!=null)
        mTextToSpeech.shutdown();
        if(progressBar.isShowing())
            progressBar.dismiss();
        active = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTextToSpeech!=null)
        mTextToSpeech.shutdown();
        if(progressBar.isShowing())
            progressBar.dismiss();
        active = false;
    }
}

