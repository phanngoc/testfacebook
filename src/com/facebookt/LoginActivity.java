package com.facebookt;
import com.bom.facebooktest.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.*;
import com.facebookt.*;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
public class LoginActivity extends Activity{
  @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		  // start Facebook Login
		  Session.openActiveSession(this, true, new Session.StatusCallback() {

		    // callback when session changes state
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		    	if (session.isOpened()) {
		    		Request.newMeRequest(session, new Request.GraphUserCallback() {

		    			  // callback after Graph API response with user object
		    			  @Override
		    			  public void onCompleted(GraphUser user, Response response) {
		    				  if (user != null) {
		    					  TextView welcome = (TextView) findViewById(R.id.welcome);
		    					  welcome.setText("Hello " + user.getName() + "!");
		    					}
		    			  }
		    			}).executeAsync();
		    	}
		    }
		  });
	}
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }
}
