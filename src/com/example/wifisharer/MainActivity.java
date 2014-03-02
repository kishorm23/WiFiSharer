package com.example.wifisharer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ipaulpro.afilechooser.utils.FileUtils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "FileChooserExampleActivity";
    private static final int REQUEST_CODE = 6384; // onActivityResult request
    public String IPAddr=null;                                              // code
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new CreateServer(this).execute();
		SocketServer server = new SocketServer();
		IPAddr = server.getLocalIpAddress();
		TextView tvIp = (TextView) findViewById(R.id.textView2);
		tvIp.setText("Your IP address: "+IPAddr);
		Button chooseFile = (Button) findViewById(R.id.button2);
        chooseFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the file chooser dialog
                showChooser();
            }
        });
        Button push = (Button) findViewById(R.id.button1);
        push.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ConnectServer();
				
			}
		});
	}
	
	private void ConnectServer()
	{ Socket socket = null;
	 DataOutputStream dataOutputStream = null;
	 DataInputStream dataInputStream = null;

	 try {
		 Log.i("CLIENT","trying to connect...");
	  socket = new Socket("10.0.2.15", 9999);
	  dataOutputStream = new DataOutputStream(socket.getOutputStream());
	  dataInputStream = new DataInputStream(socket.getInputStream());
	  dataOutputStream.writeUTF("ksks");
	  Log.i("CLIENT",dataInputStream.readUTF());
	  Log.i("CLIENT","Completed");
	 } catch (UnknownHostException e) {


		 e.printStackTrace();
	 } catch (IOException e) {
	  // TODO Auto-generated catch block
	  e.printStackTrace();
	 }
		
	}
	
	 private void showChooser() {
	        // Use the GET_CONTENT intent from the utility class
	        Intent target = FileUtils.createGetContentIntent();
	        // Create the chooser Intent
	        Intent intent = Intent.createChooser(
	                target, "Selece a file");
	        try {
	            startActivityForResult(intent, REQUEST_CODE);
	        } catch (ActivityNotFoundException e) {
	            // The reason for the existence of aFileChooser
	        }
	    }
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     Log.i("Finally","Entered");
		 switch (requestCode) {
	         case REQUEST_CODE:   
	             if (resultCode == RESULT_OK) {

	                 final Uri uri = data.getData();

	                 // Get the File path from the Uri
	                 String path = FileUtils.getPath(this, uri);
	                 Log.i("PATH",path);
	                 TextView tv = (TextView) findViewById(R.id.editText1);
	                 tv.setText(path);
	             }
	             break;
	     }
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class CreateServer extends AsyncTask<Void, Integer, Socket>
	{
		private final WeakReference<MainActivity> loginActivityWeakRef;
		
		public CreateServer(MainActivity mainActivity)
		{
			 super();
			 this.loginActivityWeakRef= new WeakReference<MainActivity >(mainActivity);
		}
		@Override
		protected Socket doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SocketServer server = new SocketServer();
			try {
				Socket socket = null;
				server.Listen();
				socket=server.socket;
				//DataInputStream dis = new DataInputStream(socket.getInputStream());
				if(socket.isClosed())
				Log.i("SOCKET","Closed");
				else Log.i("SOCKET","Not closed");
				return socket;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i("SERVER","couldn't Listen");
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(final Socket result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			DataInputStream dataInputStream;
			final String a=result.getInetAddress().toString();
			//dataInputStream = new DataInputStream(result.getInputStream());
			//String filename = dataInputStream.readUTF();
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	Log.i("TEXT","Entered:"+which);
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	break;

			        case DialogInterface.BUTTON_NEGATIVE:
			            //No button clicked
			            break;
			        }
			    }
			};

			if (loginActivityWeakRef.get() != null && !loginActivityWeakRef.get().isFinishing()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
		        Log.i("TEXT","S:"+result.getInetAddress());
			}
		}
		
	}

}
