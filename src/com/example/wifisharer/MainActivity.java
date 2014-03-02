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

import android.R.bool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "FileChooserExampleActivity";
    private static final int REQUEST_CODE = 6384; // onActivityResult request
    public String IPAddr=null;                                              // code
    public int concurrent=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new CreateServer(this).execute();
		SocketServer server = new SocketServer(this);
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

		 EditText et = (EditText) findViewById(R.id.editText1);
		 Log.i("CLIENT","trying to connect...");
	  socket = new Socket(et.getText().toString(), 9999);
	  dataOutputStream = new DataOutputStream(socket.getOutputStream());
	  dataInputStream = new DataInputStream(socket.getInputStream());
	  dataOutputStream.writeUTF("ksks");
	  Log.i("CLIENT",dataInputStream.readUTF());
	  Log.i("CLIENT","Completed");
	 } catch (UnknownHostException e) {
	  // TODO Auto-generated catch block
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
			Socket socket = null;
			DataInputStream dataInputStream = null;
			DataOutputStream dataOutputStream = null;
			Context con = null;
			ServerSocket serverSocket = null;
			boolean Selected;
			try {
				serverSocket = new ServerSocket(9999);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("SERVER","Listening on "+new SocketServer(con).getLocalIpAddress()+" 9999...");
			while(true)
			{
				try
				{
					socket = serverSocket.accept();
					dataInputStream = new DataInputStream(socket.getInputStream());
					dataOutputStream = new DataOutputStream(socket.getOutputStream());
					final Socket sos = socket;
					Log.i("SERVER","Address:"+socket.getInetAddress());
					final String filename = dataInputStream.readUTF();
					final String address = socket.getInetAddress().toString();
					dataOutputStream.writeUTF("Hello World");
					runOnUiThread(new Runnable() {
						public void run() {
							DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							    @Override
							    public void onClick(DialogInterface dialog, int which) {
							        switch (which){
							        case DialogInterface.BUTTON_POSITIVE:
							            {
							            	TextView tv =(TextView) findViewById(R.id.textView1);
							            	tv.setText("Yes selected");
							            	MainActivity.this.concurrent=1;
							            	break;
							            }

							        case DialogInterface.BUTTON_NEGATIVE:
							        {	TextView tv =(TextView) findViewById(R.id.textView1);
							            tv.setText("No selected");
							            MainActivity.this.concurrent=2;
							            break;
							        }
							        }
							    }
							};
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setMessage(address.substring(1)+" wants to share "+filename+" with you?").setPositiveButton("Accept", dialogClickListener)
							    .setNegativeButton("Decline", dialogClickListener).show();
						}
					});
					while(MainActivity.this.concurrent==0);
					switch (MainActivity.this.concurrent) {
						case 1:
						{
							dataOutputStream.writeUTF("ACK");
							break;
						}
						case 2:
						{
							dataOutputStream.writeUTF("DRP");
							break;
						}
					}
					MainActivity.this.concurrent=0;
					//socket.getInetAddress()+" wants to share "+filename+" with you?
				}
				catch (IOException e)
				{
				    // TODO Auto-generated catch block
					Log.i("SERVER","Encountered exception");
				    e.printStackTrace();
				}
				finally
				{
					if(socket!=null||dataInputStream!=null|dataOutputStream!=null)
					try
					{
						if(socket!=null) socket.close();
						if(dataInputStream!=null) dataInputStream.close();
						if(dataOutputStream!=null) dataOutputStream.close();
					}
					catch (IOException e2) {
						// TODO: handle exception
						e2.printStackTrace();
						Log.i("SERVER","Encountered exception");
					}
				}
			}
		}
		@Override
		protected void onPostExecute(Socket result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}

}
