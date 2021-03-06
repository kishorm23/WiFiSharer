package com.example.wifisharer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class RedirectActivity extends Activity
{
	private static TextSwitcher mSwitcher;
	Button btnNext;

	// Array of String to Show In TextSwitcher



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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

			setContentView(R.layout.activity_redirect);
			btnNext=(Button) findViewById(R.id.bnick);
			// get The references
			mSwitcher = (TextSwitcher) findViewById(R.id.textSwitchWc);
			mSwitcher.setFactory(new ViewFactory() {

				public View makeView() {
					// TODO Auto-generated method stub
					// create new textView and set the properties like clolr, size etc
					TextView myText = new TextView(RedirectActivity.this);
					myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
					myText.setTextSize(36);
					myText.setTextColor(Color.argb(1000,128,226,247));
					return myText;
				}
			});

			// Declare the in and out animations and initialize them 
			Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
			Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);

			// set the animation type of textSwitcher

			mSwitcher.setInAnimation(in);
			mSwitcher.setOutAnimation(out);
			//mSwitcher.setText("Welcome to WiFiSharer");
			//SetText("Welcome to WiFiSharer");
			new WaitBetweenAnimations().execute(10,300);
			new WaitBetweenAnimations().execute(20,2500);
			//mSwitcher.setText("Enter yor nick");
			btnNext = (Button) findViewById(R.id.bnick);
			btnNext.setOnClickListener(new OnClickListener() {

				@SuppressLint("ShowToast")
				@Override
				public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText et = (EditText) findViewById(R.id.etnick);
				String Nick = et.getText().toString();
				if(!Nick.equals(null)){
				mSwitcher.setText("Preference saved");
				SharedPreferences mPrefs;
				mPrefs = PreferenceManager.getDefaultSharedPreferences(RedirectActivity.this);
				SharedPreferences.Editor editor = mPrefs.edit();
				editor.putString("Nick", Nick);
				editor.putBoolean(welcomeScreenShownPref, true);
				editor.commit();
				Toast.makeText(RedirectActivity.this, "Preference saved", 3000);
				finish();
				startActivity(new Intent(RedirectActivity.this, MainActivity.class));
				}
				else
				{
					Toast.makeText(RedirectActivity.this, "Nick cannot be blank", 3000);
				}
			}
			});
		}
		else{
			finish();
			startActivity(new Intent(this, MainActivity.class));
		}

	}

	// ClickListener for NEXT button
	// When clicked on Button TextSwitcher will switch between texts
	// The current Text will go OUT and next text will come in with specified animation
	public static void SetText(String str) {
		mSwitcher.setText(str);
	}
}

class WaitBetweenAnimations extends AsyncTask<Integer, Integer, Integer>
{

	@Override
	protected Integer doInBackground(Integer... arg0) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(arg0[1]);
			return  arg0[0];
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==10) RedirectActivity.SetText("Welcome to WiFiSharer");
		else if(result==20) RedirectActivity.SetText("Enter your Nick");
	}
		
}
