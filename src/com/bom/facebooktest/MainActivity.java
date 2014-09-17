package com.bom.facebooktest;

import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.support.v7.app.ActionBarActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
	private Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;
	String APP_ID = "294317570755940";
	Button login;
	Button postowall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facebook = new Facebook(APP_ID);
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        login = (Button) findViewById(R.id.login);
        postowall = (Button) findViewById(R.id.postowall);
        FacebookLoginWindow();
        postowall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				postOnMyWall();
			}
		});
    }
    public void FacebookLoginWindow() {
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
      
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
      
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }
      
        if (!facebook.isSessionValid()) {
            facebook.authorize(this,
                    new String[] { "email", "publish_stream" },
                    new DialogListener() {
      
                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                        }
      
                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires",
                                    facebook.getAccessExpires());
                            editor.commit();
                             
                          //We got the token, so we can call postOnMyWall() here.
     
      
                        }
      
                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error
      
                        }
      
                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors
      
                        }
      
                    });
        }
    }
    private void publishStory(String hash, String title, String user) {

        Session session = Session.getActiveSession();

        if (session != null){
            // Check for publish permissions    
            List<String> permissions = session.getPermissions();
           
            Bundle postParams = new Bundle();
            postParams.putString("name", title);
            postParams.putString("caption", "bla bla");
            postParams.putString("description", "bla bla");
            postParams.putString("link", "http://blabla.com/"+hash);

            Request.Callback callback= new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                            .getGraphObject()
                            .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i("phanbom",
                                "JSON error "+ e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                      
                    } 
                }
            };

            Request request = new Request(session, "me/feed", postParams, 
                    HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }

    }
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
    public void postOnMyWall() {
    	 
    	 facebook.dialog(this, "feed", new DialogListener() {
    	  
    	        @Override
    	        public void onFacebookError(FacebookError e) {
    	        }
    	  
    	        @Override
    	        public void onError(DialogError e) {
    	        }
    	  
    	        @Override
    	        public void onComplete(Bundle values) {
    	        }
    	  
    	        @Override
    	        public void onCancel() {
    	        }
    	    });
    	}

}
