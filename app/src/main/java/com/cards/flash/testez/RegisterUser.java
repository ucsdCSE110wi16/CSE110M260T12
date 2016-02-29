package com.cards.flash.testez;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.cards.flash.testez.MainActivity;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class RegisterUser extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ParseUser user = ParseUser.getCurrentUser();

        if (user != null && ParseFacebookUtils.isLinked(user)){
            MainActivity.userName = (String)user.get("name");
            MainActivity.userEmail = user.getEmail();
            MainActivity.userId = (String)user.get("id");
            goToMainPage();
        }else {
            setContentView(R.layout.register_user_activity);
            Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Decker.ttf");
            TextView titleTextView = (TextView) findViewById(R.id.title);
            titleTextView.setTypeface(titleFont);

            Button loginButton = (Button) findViewById(R.id.login_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<String> permissions = Arrays.asList("email");
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(RegisterUser.this, permissions, new LogInCallback() {
                        @Override
                        public void done(final ParseUser parseUser, ParseException e) {
                            System.out.println(parseUser);
                            if (parseUser == null){
                                Toast.makeText(RegisterUser.this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }else {
                                GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            public void onCompleted(JSONObject json, GraphResponse response) {
                                                try {
                                                    JSONObject jsonObject = response.getJSONObject();
                                                    MainActivity.userId = jsonObject.getString("id");
                                                    MainActivity.userEmail = jsonObject.getString("email");
                                                    MainActivity.userName = jsonObject.getString("name");

                                                    if (parseUser.isNew()) {
                                                        parseUser.put("name", MainActivity.userName);
                                                        parseUser.setEmail(MainActivity.userEmail);
                                                        parseUser.put("id", MainActivity.userId);
                                                        parseUser.save();

                                                        ParseObject object = new ParseObject("UserToCategory");
                                                        object.put("userId", parseUser.getObjectId());
                                                        object.save();
                                                    }
                                                    goToMainPage();

                                                } catch (Exception e) {
                                                    ParseFacebookUtils.unlinkInBackground(parseUser);
                                                    Toast.makeText(RegisterUser.this, e.getMessage(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "name,email,id");
                                graphRequest.setParameters(parameters);
                                graphRequest.executeAsync();
                            }

                        }
                    });
                }
            });

        }
    }

    private void goToMainPage(){
        Intent intent = new Intent(RegisterUser.this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
