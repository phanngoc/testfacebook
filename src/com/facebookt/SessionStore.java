package com.facebookt;

import com.facebook.android.Facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

@SuppressWarnings("deprecation")
public class SessionStore {

    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String LAST_UPDATE = "last_update";
    public static final String KEY = "facebook-session";

    /*
     * Save the access token and expiry date so you don't have to fetch it each
     * time
     */
    public static boolean save(Facebook session, Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        Log.d("phanbom","Session store save:"+ session.getAccessToken());
        editor.putString(TOKEN, session.getAccessToken());
        editor.putString(EXPIRES, session.getAccessExpires()+"");
        editor.putLong(LAST_UPDATE, session.getLastAccessUpdate());
        return editor.commit();
    }

    /*
     * Restore the access token and the expiry date from the shared preferences.
     */
    public static boolean restore(Facebook session, Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        Log.d("phanbom","Session store:"+ savedSession.getString(TOKEN, null));
        session.setTokenFromCache(
                savedSession.getString(TOKEN, null),
                savedSession.getLong(EXPIRES, 0),
                savedSession.getLong(LAST_UPDATE, 0));
   
        return session.isSessionValid();
    }

    public static void clear(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

}