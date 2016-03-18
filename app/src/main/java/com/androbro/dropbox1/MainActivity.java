package com.androbro.dropbox1;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.TokenPair;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout container;
    private DropboxAPI dropboxAPI;
    private boolean isUserLoggedIn;
    private Button loginButton;
    private Button uploadFileButton;
    private Button listFileButton;

    private static final String DROPBOX_FILE_DIR = "/DropBox1/";
    private static final String DROPBOX_NAME = "dropbox_prefs";
    private static final String ACCESS_KEY = "7kuofgj8cbqz62k";
    private static final String ACCESS_SECRET = "8xoff41ihln73c8";
    private static final Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(this);
        uploadFileButton = (Button) findViewById(R.id.uploadFileBtn);
        uploadFileButton.setOnClickListener(this);
        listFileButton = (Button) findViewById(R.id.listFilesBtn);
        listFileButton.setOnClickListener(this);

        container = (LinearLayout) findViewById(R.id.container_files);

        loggedIn(false);

        AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if (key != null && secret != null){

            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);

        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        dropboxAPI = new DropboxAPI(session);

    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = (AndroidAuthSession) dropboxAPI.getSession();
        if (session.authenticationSuccessful()){
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();

                loggedIn(true);

            } catch (IllegalStateException e){

                Toast.makeText(this, "DropBox auth error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void loggedIn(boolean userLoggedIn) {

        isUserLoggedIn = userLoggedIn;

        uploadFileButton.setEnabled(userLoggedIn);
        uploadFileButton.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);

        listFileButton.setEnabled(userLoggedIn);
        listFileButton.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);

        loginButton.setText(userLoggedIn ? "Logout" : "Login");


    }


    private final Handler handler = new Handler() {

        public void handleMessage(Message message){
            ArrayList<String> result = message.getData().getStringArrayList("data");

            for (String fileName: result){
                TextView textView = new TextView(MainActivity.this);
                textView.setText(fileName);
                container.addView(textView);
            }
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.loginBtn:
                if (isUserLoggedIn){
                    dropboxAPI.getSession().unlink();
                    loggedIn(false);
                }else {
                    ((AndroidAuthSession)dropboxAPI.getSession()).startAuthentication(MainActivity.this);
                }
                break;

            case R.id.uploadFileBtn:
                UploadFile uploadFile = new UploadFile(this, dropboxAPI, DROPBOX_FILE_DIR);
                uploadFile.execute();
                break;

            case R.id.listFilesBtn:
                ListFiles listFiles = new ListFiles(dropboxAPI, DROPBOX_FILE_DIR, handler);
                listFiles.execute();
                break;
            default:
                break;
        }

    }
}
