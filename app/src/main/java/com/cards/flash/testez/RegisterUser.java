package com.cards.flash.testez;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
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
        loginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                //do nothing
            }

            @Override
            public void onError(FacebookException exception) {
                new AlertDialog.Builder(RegisterUser.this)
                        .setTitle("Failure")
                        .setMessage("Unable to login. Please try again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //hide it
                            }
                        }).create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
    }
}
