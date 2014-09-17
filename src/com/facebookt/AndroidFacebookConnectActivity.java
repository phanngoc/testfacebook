package com.facebookt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bom.facebooktest.R;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class AndroidFacebookConnectActivity extends Activity {
	 
    // Your Facebook APP ID
    private static String APP_ID = "294317570755940"; // Replace your App ID here
 
    // Instance of Facebook Class
    private Facebook facebook;
    private AsyncFacebookRunner mAsyncRunner;
    String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;
    private Button btnFbLogin;
    private Button btnPostToWall;
    

    private String messageToPost;
    private static final String[] PERMISSIONS = new String[] { "publish_stream" };
    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginface1);
  
        facebook = new Facebook(APP_ID);
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        btnFbLogin = (Button)findViewById(R.id.buttonlogin);
        btnPostToWall = (Button)findViewById(R.id.postowall);
        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            	Facebook facebook = new Facebook(APP_ID);
	            	restoreCredentials(facebook);
	            	messageToPost = "Hello Everyone.";
	            	if (!facebook.isSessionValid()) {
	            	            loginAndPostToWall();
	            	} else {
	            	            postToWall(messageToPost);
	            	}
            }
        });
        btnPostToWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	postToWall(messageToPost);
            } 
        }); 
        Log.d("phanbom","Key hash:"+printKeyHash(this));
    } 
    
    
    public boolean saveCredentials(Facebook facebook) {
        Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, facebook.getAccessToken());
        editor.putLong(EXPIRES, facebook.getAccessExpires());
        return editor.commit();
    }

    public boolean restoreCredentials(Facebook facebook) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
        facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
        facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
        return facebook.isSessionValid();
    }

    public void loginAndPostToWall() {
        facebook.authorize(this, PERMISSIONS,Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
    }

    
    
    public void postToWall(String message) {
        Bundle parameters = new Bundle();
        parameters.putString("message", message);
        parameters.putString("description", "topic share");
        try {
            //facebook.request("me");
            String response = facebook.request("me/feed", parameters, "POST");
            Log.d("Tests", "got response: " + response);
            if (response == null || response.equals("") || response.equals("false")) {
                showToast("Blank response.");
            } else {
                showToast("Message posted to your facebook wall!");
            }
        } catch (Exception e) {
            showToast("Failed to post to wall!");
            e.printStackTrace();
        }
    }

    class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
        	 Log.d("phanbom", "onComplete LoginDialogListener");
            saveCredentials(facebook);
           
  
            if (messageToPost != null) {
                postToWall(messageToPost);
            }
        }

        public void onFacebookError(FacebookError error) {
            showToast("Authentication with Facebook failed!");
        }

        public void onError(DialogError error) {
            showToast("Authentication with Facebook failed!");
        }

        public void onCancel() {
            showToast("Authentication with Facebook cancelled!");
        }
    }

	    private void showToast(String message) {
	        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
	                    .show();
	    }
    
    
    
    
    
    public static String printKeyHash(Activity context) {
		PackageInfo packageInfo;
		String key = null;
		try {

			//getting application package name, as defined in manifest
			String packageName = context.getApplicationContext().getPackageName();

			//Retriving package info
			packageInfo = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
			
			Log.e("Package Name=", context.getApplicationContext().getPackageName());
			
			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				key = new String(Base64.encode(md.digest(), 0));
			
				// String key = new String(Base64.encodeBytes(md.digest()));
				Log.e("Key Hash=", key);

			}
		} catch (NameNotFoundException e1) {
			Log.e("Name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) {
			Log.e("No such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}

		return key;
	}
    
    
 
}
