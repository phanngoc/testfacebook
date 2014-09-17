package com.facebookt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bom.facebooktest.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;


/**
 * This example shows how to post status to Facebook wall.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * http://www.londatiga.net
 */
public class TestPost extends Activity{
	private Facebook mFacebook;
	private CheckBox mFacebookCb;
	private ProgressDialog mProgress;
	
	private Handler mRunOnUi = new Handler();
	
	private static final String APP_ID = "294317570755940";

	AccessToken token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.post);
		
		final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
		mFacebookCb				  = (CheckBox) findViewById(R.id.cb_facebook);
		
		mProgress	= new ProgressDialog(this);
		
		mFacebook 	= new Facebook(APP_ID);
		
		//SessionStore.restore(mFacebook, this);
		
		SharedPreferences savedSession = getApplicationContext().getSharedPreferences(SessionStore.KEY, Context.MODE_PRIVATE);

		Date date=null;
        try {
            date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(savedSession.getString(SessionStore.EXPIRES,"0"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		token = AccessToken.createFromExistingAccessToken(savedSession.getString(SessionStore.TOKEN, null),date, null, AccessTokenSource.TEST_USER, null);
		postNew();
		//Log.d("phanbom","mfacebook da co session chua :"+mFacebook.getAccessToken()+"|"+mFacebook.isSessionValid());
		
		if (mFacebook.isSessionValid()) {
			mFacebookCb.setChecked(true);
				
			String name = "dsdd";
			name		= (name.equals("")) ? "Unknown" : name;
				
			mFacebookCb.setText("  Facebook  (" + name + ")");
		}
		
		((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String review = reviewEdit.getText().toString();
				
				if (review.equals("")) return;
			
				//if (mFacebookCb.isChecked()) postToFacebook(review);
			}
		});
		
		
   }	
	public void postNew()
	{
			Session.openActiveSessionWithAccessToken(this,token,new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				// TODO Auto-generated method stub
			
            // callback when session changes state
				   if (session.isOpened()) {


                       // make request to the /me API
                       Request.newMeRequest(session, new Request.GraphUserCallback() {

                         // callback after Graph API response with user object
                         @Override
                         public void onCompleted(GraphUser user, Response response) {
                           if (user != null) {
                        	   Log.d("phanbom","tiep theo ne:"+user.getName()+"|"+user.getUsername());
                  
                           }
                         }
			          }).executeAsync();
                       GraphPlace place;
                       Request request = Request
                               .newStatusUpdateRequest(Session.getActiveSession(), "daydaydayday",new Request.Callback() {
                                   @Override
                                   public void onCompleted(Response response) {
                                	   Log.d("phanbom","respone ::::"+response.toString());
                                       //showPublishResult(message, response.getGraphObject(), response.getError());
                                   }
                               });
                       request.executeAsync();
                       
				  }
        
			}
			});
}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

	private void postToFacebook(String review) {	
		mProgress.setMessage("Posting ...");
		mProgress.show();
		
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		
		Bundle params = new Bundle();
    		
		params.putString("message", review);
		params.putString("name", "Dexter");
		params.putString("caption", "londatiga.net");
		params.putString("link", "http://www.londatiga.net");
		params.putString("description", "Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
		params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");
		
		mAsyncFbRunner.request("me/feed", params,"POST",new WallPostListener(),new Object());
	}

	private final class WallPostListener implements AsyncFacebookRunner.RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			Log.d("phanbom", "Test response ne:"+response);
			mRunOnUi.post(new Runnable() {
        		@Override
        		public void run() {
        			mProgress.cancel();
        			
        			Toast.makeText(TestPost.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
        		}
        	});
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    }
}