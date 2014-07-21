package com.example.wifisharer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ProgressActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_redirect);
		
		SharedPreferences mPrefs;
		final String welcomeScreenShownPref = "welcomeScreenShown";

		    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		    // second argument is the default to use if the preference can't be found
		    Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

		    if (!welcomeScreenShown) {
		    	setContentView(R.layout.activity_redirect);
		        SharedPreferences.Editor editor = mPrefs.edit();
		        editor.putBoolean(welcomeScreenShownPref, true);
		        editor.commit();
		    }
		    else
		    {
		    	startActivity(new Intent(this, MainActivity.class));
		    }

		}
		
}
