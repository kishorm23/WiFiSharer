package com.example.wifisharer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ipaulpro.afilechooser.utils.FileUtils;

import android.R.bool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
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
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				 EditText et2 = (EditText) findViewById(R.id.editText2);
				 EditText et1 = (EditText) findViewById(R.id.editText1);
				 String pathName = et1.getText().toString();
				 String IpAddr = et2.getText().toString();
				 Log.i("CLIENT","just Inside Async Task");
				 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				    new ClientSide().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pathName,IpAddr);
				 else
				     new ClientSide().execute(pathName,IpAddr);
				
			}
		});
	}
	
	private void ConnectServer(String pathName,String IpAddr)
	{ Socket socket = null;
	 DataOutputStream dataOutputStream = null;
	 DataInputStream dataInputStream = null;

	 try {
		 Log.i("CLIENT","trying to connect...");
			Log.i("CLIENT",IpAddr);
		if(IpAddr.equals(null)) IpAddr="127.0.0.1";

	  socket = new Socket(IpAddr, 9999);
	  dataOutputStream = new DataOutputStream(socket.getOutputStream());
	  dataInputStream = new DataInputStream(socket.getInputStream());
	  String fileName = new File(pathName).getName();
	  dataOutputStream.writeUTF(fileName);
	  String response = dataInputStream.readUTF();
	  if(response.equals("ACK"))
	  {
		  Log.i("CLIENT","Sending file");
			Log.i("CLIENT","step 2");
		  File SendFile = new File (pathName);
		  Log.i("CLIENT","Length:"+SendFile.length());
		  int a=(int) SendFile.length();
			Log.i("CLIENT","step 3");
		  byte [] bytearray = new byte[a];
			int resource;
			Log.i("CLIENT","step 4");
		  dataOutputStream.writeInt((int) SendFile.length());
			Log.i("CLIENT","step 5");
		  FileInputStream fis = new FileInputStream(SendFile);
			Log.i("CLIENT","step 6");
			/*for(int i=0;i<a;i++)
			{
				resource = fis.read();
				Log.i("CLIENT",resource);
				dataOutputStream.writeByte(resource);
			}*/
		  /*BufferedInputStream bis = new BufferedInputStream(fis);
		  bis.read(bytearray, 0, bytearray.length);
		  OutputStream os = socket.getOutputStream();
		  os.write(bytearray, 0, bytearray.length);
		  os.flush();*/
		  fis.read(bytearray,0,(int) SendFile.length());
		 
		  Log.i("CLIENT",bytearray.toString()+" "+bytearray.length+" "+ SendFile.toString());
		  //dataOutputStream.write(bytearray,0,bytearray.length);
		  //fis.read(bytearray);
		  //Log.i("CLIENT","Bytes array:"+bytearray.toString());
		  StringBuilder text = new StringBuilder();

		 /*try {
		      BufferedReader br = new BufferedReader(new FileReader(pathName));
		      String line;

		      while ((line = br.readLine()) != null) {
		          text.append(line);
		          text.append('\n');
		      }
		  }
		  finally
		  {
			  Log.i("CLIENT",text.toString());
			  dataOutputStream.writeUTF(text.toString());
		  }*/
		  dataOutputStream.write(bytearray);
	  }
	  Log.i("CLIENT",response);
	  Log.i("CLIENT","Completed");
	 } catch (UnknownHostException e) {
	  // TODO Auto-generated catch block
		 Toast.makeText(getApplication(), "No route to host", 2000);
	  e.printStackTrace();
	 } catch (IOException e) {
	  // TODO Auto-generated catch block
		 Toast.makeText(getApplication(), "No route to host", 2000);
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
	public class ClientSide extends AsyncTask<String, Integer, Void>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Toast.makeText(getBaseContext(), "Execute ho gayi", 2000).show();
		}
		@Override
		protected Void doInBackground(String... params) {
			Log.i("CLIENT","FUCKED UP");
			ConnectServer(params[0],params[1]);
			return null;
		}
		protected void onPostExecute() {
			// TODO Auto-generated method stub
			
		}
		
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
					dataOutputStream.writeUTF("ACK");
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
					while(MainActivity.this.concurrent==0)
					{
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					switch (MainActivity.this.concurrent) {
						case 1:
						{
							//dataOutputStream.writeUTF("ACK");
							int current=0; 
							Log.i("SERVER","In the zone");
							Log.i("SERVER","Socket closed:"+socket.isClosed());
							int filesize=dataInputStream.readInt();
							Log.i("SERVER","filesize length:"+filesize);
							 byte [] mybytearray  = new byte [filesize];
							 dataInputStream.read(mybytearray);
							 Log.i("SERVER",mybytearray[0]+", "+mybytearray[1]+", "+mybytearray[2]);
					         //InputStream is = socket.getInputStream();

					         //FileOutputStream fos = new FileOutputStream("/mnt/sdcard/as.txt"); // destination path and name of file
					         //BufferedOutputStream bos = new BufferedOutputStream(fos);
					          /*int  bytesRead = is.read(mybytearray,0,mybytearray.length);
					          current = bytesRead;
					          do {
					               bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
					               if(bytesRead >= 0) current += bytesRead;
					               Log.i("SERVER","DATA:"+current+" "+mybytearray.toString());
					            } while(bytesRead > -1);*/
					            //bos.write(mybytearray, 0 , current);
					            //bos.flush();
					            //bos.close();
							 //dataInputStream.read(mybytearray);
							 //Log.i("SERVER",file);
							 FileOutputStream fos = new FileOutputStream("//mnt//sdcard//Shared//"+filename+"sd");
							 fos.write(mybytearray);
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
