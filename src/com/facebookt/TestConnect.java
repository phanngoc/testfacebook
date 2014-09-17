package com.facebookt;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bom.facebooktest.R;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import com.facebook.android.Facebook.DialogListener;


/**
 * This example shows how to connect to Facebook, display authorization dialog and save user's access token
 * and username.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * http://www.londatiga.net
 */

public class TestConnect extends Activity {
	private Facebook mFacebook;
	private CheckBox mFacebookBtn;
	private ProgressDialog mProgress;
	
	private static final String[] PERMISSIONS = new String[] {"publish_stream", "read_stream", "offline_access"};
	
	private static final String APP_ID = "294317570755940";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        mFacebookBtn	= (CheckBox) findViewById(R.id.cb_facebook);
        
        mProgress		= new ProgressDialog(this);
        mFacebook		= new Facebook(APP_ID);
        
        //SessionStore.restore(mFacebook, this);
        
        if (mFacebook.isSessionValid()) {
			mFacebookBtn.setChecked(true);
			
			
			mFacebookBtn.setTextColor(Color.WHITE);
		}
        
        mFacebookBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onFacebookClick();
			}
		});
        
        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TestConnect.this, TestPost.class));
			}
		});
    } 
        
    private void onFacebookClick() {
		if (mFacebook.isSessionValid()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setMessage("Delete current Facebook connection?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   fbLogout();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel(); 
			                mFacebookBtn.setChecked(true);
			           }
			       });
			
			final AlertDialog alert = builder.create();
			
			alert.show();
		} else {
			mFacebookBtn.setChecked(false);
			Log.d("phanbom", "autho ne");
			mFacebook.authorize(this, PERMISSIONS, -1, new FbLoginDialogListener());
		}
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
    private final class FbLoginDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			Log.d("phanbom", "facebook ne");
            SessionStore.save(mFacebook, TestConnect.this);
            mFacebookBtn.setText("  Facebook (No Name)");
            mFacebookBtn.setChecked(true);
			mFacebookBtn.setTextColor(Color.WHITE);
			  
            getFbName();
		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			 Toast.makeText(TestConnect.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();
	           
	           mFacebookBtn.setChecked(false);
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			Toast.makeText(TestConnect.this, "Facebook connection failed", Toast.LENGTH_SHORT).show(); 
        	
        	mFacebookBtn.setChecked(false);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			mFacebookBtn.setChecked(false);
		}

    }
    
	private void getFbName() {
		postToWall();
		
		mProgress.setMessage("Finalizing ...");
		mProgress.show();
		
		new Thread() {
			@Override
			public void run() {
		        String name = "";
		        int what = 1;
		        
		        try {
		        	String me = mFacebook.request("me");
		        	
		        	JSONObject jsonObj = (JSONObject) new JSONTokener(me).nextValue();
		        	name = jsonObj.getString("name");
		        	Log.d("phanbom","name fb"+name);
		        	what = 0;
		        } catch (Exception ex) {
		        	ex.printStackTrace();
		        }
		        
		        mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
			}
		}.start();
	}
	
	private void fbLogout() {
		mProgress.setMessage("Disconnecting from Facebook");
		mProgress.show();
			
		new Thread() {
			@Override
			public void run() {
				SessionStore.clear(TestConnect.this);
		        	   
				int what = 1;
					
		        try {
		        	mFacebook.logout(TestConnect.this);
		        		 
		        	what = 0;
		        } catch (Exception ex) {
		        	ex.printStackTrace();
		        }
		        	
		        mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}
	
	private Handler mFbHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();
			
			if (msg.what == 0) {
				String username = (String) msg.obj;
		        username = (username.equals("")) ? "No Name" : username;
		            
		        
		        
		        mFacebookBtn.setText("  Facebook (" + username + ")");
		         
		        Toast.makeText(TestConnect.this, "Connected to Facebook as " + username, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TestConnect.this, "Connected to Facebook", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();
			
			if (msg.what == 1) {
				Toast.makeText(TestConnect.this, "Facebook logout failed", Toast.LENGTH_SHORT).show();
			} else {
				mFacebookBtn.setChecked(false);
	        	mFacebookBtn.setText("  Facebook (Not connected)");
	        	mFacebookBtn.setTextColor(Color.GRAY);
	        	   
				Toast.makeText(TestConnect.this, "Disconnected from Facebook", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private void postToWall()
	{
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		 
		Bundle params = new Bundle();
    		
		params.putString("message", "sdsddsdsd");
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