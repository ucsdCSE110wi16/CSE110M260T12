package com.cards.flash.testez;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.cards.flash.testez.MainActivity;
import java.util.Arrays;
import java.util.List;

public class RegisterUser extends Activity{

    CallbackManager callBackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);

        setContentView(R.layout.register_user_activity);
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Decker.ttf");
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setTypeface(titleFont);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissions = Arrays.asList("email","public_profile");
        loginButton.setReadPermissions(permissions);
        callBackManager = CallbackManager.Factory.create();
        Intent intent = new Intent(RegisterUser.this, MainActivity.class);
        startActivity(intent);
        loginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                startActivity(intent);
                /*GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback(){

                            @Override
                            public void onCompleted(JSONArray objects, GraphResponse response) {
                                try{
                                    JSONObject jsonObject = response.getJSONObject();
                                    MainActivity.userEmail = jsonObject.getString("email");
                                    MainActivity.userName = jsonObject.getString("name");
                                    MainActivity.userPicLink = jsonObject.getString("link");

                                    //Intent
                                }catch (Exception e){
                                    Toast.makeText(RegisterUser.this, "Unable to fetch all data from Facebook",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });*/

            }

            @Override
            public void onCancel() {
                //do nothing
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(RegisterUser.this, "Unable to login. Please try again.",
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
    }
}
