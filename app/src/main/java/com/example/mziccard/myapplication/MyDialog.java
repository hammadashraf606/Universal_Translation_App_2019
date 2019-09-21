package com.example.mziccard.myapplication;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


public class MyDialog extends Activity implements TextToSpeech.OnInitListener, InternetConnectivityListener {


    private static final String LOG_TAG = MyDialog.class.getName() ;
    public static boolean active = false;
    HashMap<String, String> map = new HashMap<>();
    public static Activity myDialog;
    private static final String API_KEY = "AIzaSyDZJtBthHsstFZA0omlA-vjNoln-w2H1Ic";
    static ArrayAdapter<String> langAdapter;
    static ArrayList<String> languageCodes = new ArrayList<String>();
    final Handler textViewHandler = new Handler();
    static ProgressDialog progressBar;
    private Dialog process_tts;                             //    Dialog box for Text to Speech Engine Language Switch
    private TextToSpeech mTextToSpeech;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    GetLanguages getLanguages;
    static List<String> languageNames = new ArrayList<String>();
    EditText edt;
    Button btn;
    static SearchableSpinner language;
    CircleImageView paste, copy, speech;
    TextView textview;
    View top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog);
        getLanguages=new GetLanguages();

        edt = (EditText) findViewById(R.id.dialogetsample);
        btn = (Button) findViewById(R.id.dialogbtnTranslate);
        top = (View) findViewById(R.id.dialog_top);
        language = (SearchableSpinner) findViewById(R.id.dialogspnTarget);
        paste = (CircleImageView) findViewById(R.id.dialogbtnPaste);
        copy = (CircleImageView) findViewById(R.id.dialogbtnCopy);
        speech = (CircleImageView) findViewById(R.id.dialogbtnTTS);
        textview=(TextView)findViewById(R.id.dialog_text_view);
//        getLanguages.execute();
        myDialog = MyDialog.this;

        if (!isOnline()) {
            Toasty.error(MyDialog.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                stopService(new Intent(MyDialog.this, ChatHeadService23.class));
                startActivity(intent);
                //finish();
                //System.exit(0);
            }
            else {
                stopService(new Intent(MyDialog.this, ChatHeadService.class));
                startActivity(intent);
                //mainActivity.setVisible(false);
                //finish();
                //System.exit(0);
            }

        } else {
            Toasty.success(MyDialog.this, "Internet Connected!", Toast.LENGTH_SHORT, true).show();
        }
        progressBar = new ProgressDialog(MyDialog.this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading Languages");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (isOnline()) {

            progressBar.show();
            getLanguages.execute();
        }

        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edt.getText().toString().isEmpty()) {
                    Toasty.error(MyDialog.this, "Please Enter Text", Toast.LENGTH_SHORT, true).show();

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
                                    translate.translate(edt.getText().toString(),
                                            Translate.TranslateOption.targetLanguage(langcode));
                            textViewHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (textview != null) {
                                        textview.setText(translation.getTranslatedText());
                                    }
                                }
                            });
                            return null;
                        }
                    }.execute();
                }
            }
        });

        //COPY
        copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textview.getText().toString();
                if (text.isEmpty()) {
                    Toasty.info(MyDialog.this, "No text to copy", Toast.LENGTH_SHORT, true).show();


                } else {
                    myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    myClip = ClipData.newPlainText("text", text);
                    myClipboard.setPrimaryClip(myClip);
                    Toasty.success(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT, true).show();

            }
        }});


        //TRANSLATE ON LANGUAGE CHANGE
        language.setTitle("Select Target Language");
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                translate.translate(edt.getText().toString(),
                                        Translate.TranslateOption.targetLanguage(langcode));
                        textViewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (textview != null) {
                                    textview.setText(translation.getTranslatedText());
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


        top.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });




        //TEXT TO SPEECH
        process_tts = new Dialog(MyDialog.this);
        process_tts.setContentView(R.layout.dialog_processing_tts);
        process_tts.setTitle(getString(R.string.process_tts));
        try {
            mTextToSpeech = new TextToSpeech(this, this);
        }catch (Exception e)
        {
            Log.e("TTS FAILURE====",e.getMessage());
        }
        speech.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTextToSpeech!=null)
                speakOut();
            }
        });


        //PASTE
        paste.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String pasteData = "";

                // If it does contain data, decide if you can handle the data.
                assert clipboard != null;
                if (!(clipboard.hasPrimaryClip())) {
                    Toasty.info(MyDialog.this, "Nothing to paste", Toast.LENGTH_SHORT, true).show();

                } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

                    Toasty.info(MyDialog.this, "Copied data is not text", Toast.LENGTH_SHORT, true).show();

                    // since the clipboard has data but it is not plain text

                } else {

                    //since the clipboard contains plain text.
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                    // Gets the clipboard as text.
                    pasteData = item.getText().toString();
                    edt.setText(pasteData);
                }
            }
        });

    }




    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            startService(new Intent(MyDialog.this, ChatHeadService23.class));
            //finish();
            //System.exit(0);
        }
        else {
            startService(new Intent(MyDialog.this, ChatHeadService.class));
            //mainActivity.setVisible(false);
            //finish();
            //System.exit(0);
        }
        active = true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(mTextToSpeech!=null)
        mTextToSpeech.shutdown();
        if (progressBar.isShowing())
            progressBar.dismiss();
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (progressBar.isShowing())
            progressBar.dismiss();
        if(mTextToSpeech!=null)
        mTextToSpeech.shutdown();
        active = false;
    }

    @Override
    public void onInit(int i) {
        Log.e("Inside----->", "onInit");
        if (i == TextToSpeech.SUCCESS && isOnline()) {
            int index = language.getSelectedItemPosition();
            String langcode = languageCodes.get(index);
            int result = mTextToSpeech.setLanguage(new Locale(langcode));
            if (result == TextToSpeech.LANG_MISSING_DATA)
            {
                Toasty.error(getApplicationContext(), "Language Pack Missing", Toast.LENGTH_SHORT, true).show();


            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toasty.error(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT, true).show();


            }
            speech.setEnabled(true);
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
            Toasty.error(MyDialog.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected && getLanguages.getStatus() == AsyncTask.Status.FINISHED || getLanguages.getStatus() == AsyncTask.Status.RUNNING)
        {
            if(active)
            {
                Intent intent= new Intent(myDialog,MyDialog.class);
                startActivity(intent);
                //finish();
            }
//            else if (MyDialog.active)
//            {
//                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
//                    startService(new Intent(MainActivity.this, ChatHeadService23.class));
//                    finish();
//                    //System.exit(0);
//                }
//                else {
//                    startService(new Intent(MainActivity.this, ChatHeadService.class));
//                    //mainActivity.setVisible(false);
//                    finish();
//                    //System.exit(0);
//                }
//            }
////            try {
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
            Toasty.error(MyDialog.this, "Sorry there is no internet connection", Toast.LENGTH_SHORT, true).show();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
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
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }

            return languageNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            langAdapter = new ArrayAdapter<String>(myDialog, android.R.layout.simple_spinner_dropdown_item, result);
            //langAdapterDark = new ArrayAdapter<String>(MainActivity.this, R.layout.spinnertextwhite, result);
            langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            language.setBackgroundColor(getResources().getColor(android.R.color.white));
            language.setPopupBackgroundDrawable(myDialog.getResources().getDrawable(R.drawable.roundedwhite));
//            //langAdapterDark.setDropDownViewResource(R.layout.spinnertextwhite);
//            if(darkthemeflag)
//            {
//                language.setAdapter(langAdapterDark);
//                srclang.setAdapter(langAdapterDark);
//            }
//            else {
//                language.setAdapter(langAdapter);
//                srclang.setAdapter(langAdapter);
//            }
//            srclang.setSelection(21);
            language.setAdapter(langAdapter);
            language.setSelection(21);
            for (String s : languageNames
                    ) {

                Toasty.info(myDialog, s, Toast.LENGTH_SHORT, true).show();

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

    //  TEXT TO SPEECH ACTION
    @SuppressWarnings("deprecation")
    private void speakOut() {
        String lang = language.getSelectedItem().toString();
        int index = language.getSelectedItemPosition();
        String langcode = languageCodes.get(index);
        Toasty.info(this, lang, Toast.LENGTH_SHORT, true).show();

        int result = mTextToSpeech.setLanguage(new Locale(langcode));
        Log.e("Inside", "speakOut " + result);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Toasty.error(getApplicationContext(), "Language Pack Missing", Toast.LENGTH_SHORT, true).show();

            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toasty.error(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT, true).show();

        } else {
            String textMessage = textview.getText().toString();
            process_tts.show();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            mTextToSpeech.setSpeechRate(0.75f);
            mTextToSpeech.speak(textMessage, TextToSpeech.QUEUE_FLUSH, map);
            process_tts.dismiss();

        }
        //process_tts.hide();
    }


}
