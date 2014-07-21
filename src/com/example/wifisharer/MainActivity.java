package com.example.wifisharer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 6384; // onActivityResult request
    public String IPAddr=null;                                              // code
    public int concurrent=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new CreateServer().execute();
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
        
        final Pattern PARTIAl_IP_ADDRESS =
                Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                                 "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$"); 
        EditText et2 = (EditText) findViewById(R.id.editText2);
        et2.addTextChangedListener(new TextWatcher() {                       
          @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}            
          @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}            

          private String mPreviousText = "";          
          @Override
          public void afterTextChanged(Editable s) {          
              if(PARTIAl_IP_ADDRESS.matcher(s).matches()) {
                  mPreviousText = s.toString();
              } else {
                  s.replace(0, s.length(), mPreviousText);
              }
          }
      });
	}
	
		
	 private void showChooser() {
	        Intent target = FileUtils.createGetContentIntent();
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
	                 EditText tv = (EditText) findViewById(R.id.editText1);
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
			case R.id.action_scan:
			{
				startActivity(new Intent(this, ScanActivity.class));
				break;
			}
			case R.id.action_settings:
				break;
		}
			return super.onOptionsItemSelected(item);
	}
	@SuppressLint({ "DefaultLocale", "ShowToast" })
	public class ClientSide extends AsyncTask<String, Integer, Long>
	{
		ProgressDialog dialog;
		boolean accepted=false;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			EditText et1 = (EditText) findViewById(R.id.editText1);
			String pathName = et1.getText().toString();
			File SendFile = new File (pathName);
			String fileName = SendFile.getName();
			Log.i("CLIENT","In pre execute"); 
		    dialog = new ProgressDialog(MainActivity.this);
		    dialog.setMessage("Sending "+fileName+"...");
		    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    dialog.setMax(100);
		    dialog.setCancelable(true);
		    dialog.show();
		}
		@Override
		protected Long doInBackground(String... params) {
			//long size = ConnectServer(params[0],params[1]);
			String IpAddr = params[1];
			String pathName = params[0];
			Socket socket = null;
			DataOutputStream dataOutputStream = null;
			DataInputStream dataInputStream = null;
			try
			{
				Log.i("CLIENT","trying to connect...");
				Log.i("CLIENT",IpAddr);
				
				if(IpAddr.equals(null)) IpAddr="127.0.0.1";
				
				socket = new Socket(IpAddr, 9999);
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());
				File SendFile = new File (pathName);
				String fileName = SendFile.getName();
				long fileSize = SendFile.length();
				dataOutputStream.writeInt(01);
				dataOutputStream.writeUTF(fileName);
				String response = dataInputStream.readUTF();
				
				if(response.equals("ACK"))
				{
					accepted = true;
					Log.i("CLIENT","Sending file");
					Log.i("CLIENT","Length:"+SendFile.length());
					byte [] bytearray = new byte[65536];
					dataOutputStream.writeInt((int) fileSize);
					FileInputStream fis = new FileInputStream(SendFile);
					int count=0;
					while(count<fileSize)
					{
						Log.i("CLIENT",count+" "+fileSize);
						if(fileSize-count>65536)
						{
							fis.read(bytearray,0,65536);
							dataOutputStream.write(bytearray, 0, 65536);
						}
						else
						{
							fis.read(bytearray,0,(int) (fileSize-count));
							dataOutputStream.write(bytearray, 0, (int) (fileSize-count));
						}
						count=count+65536;
						this.publishProgress(count,(int) fileSize);
					}
					Log.i("CLIENT",bytearray.toString()+" "+bytearray.length+" "+ SendFile.toString());
					Log.i("CLIENT",bytearray[0]+", "+bytearray[1]+", "+bytearray[2]);
					socket.close();
				}
				Log.i("CLIENT",response);
				Log.i("CLIENT","Completed");
				dataInputStream.close();
				dataOutputStream.close();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				socket.close();
				return fileSize;
		 } catch (UnknownHostException e) {
		  // TODO Auto-generated catch block
			 Log.i("CLIENT","Exception occured");
		  e.printStackTrace();
		 } catch (IOException e) {
		  // TODO Auto-generated catch block
			 Log.i("CLIENT","Exception occured");
		  e.printStackTrace();
		 }
			return (long) 0;
		}
	    protected void onProgressUpdate(Integer... progress) { 
	    	float fprogress = (progress[0]/progress[1]);
	    	Log.i("CLIENT","Inside publishProg "+progress[1]+", "+progress[0]+", "+fprogress*100);
	    	int sProgress = (int)(((double)progress[0]/(double)progress[1]) * 100);;
	    	Log.i("CLIENT","Progress:"+sProgress);
	        dialog.setProgress(sProgress);
	        }
		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			if(accepted) Toast.makeText(getBaseContext(), "File Sent: Total data transfer "+humanReadableByteCount(result, true)+".", 2000).show();
			else Toast.makeText(getBaseContext(), "Peer refused to accept the file.", 2000).show();
		}
		
		public String humanReadableByteCount(long bytes, boolean si) {
		    int unit = si ? 1000 : 1024;
		    if (bytes < unit) return bytes + " B";
		    int exp = (int) (Math.log(bytes) / Math.log(unit));
		    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "");
		    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
		}
		
	}
	public class CreateServer extends AsyncTask<Void, Integer, Socket>
	{
		ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.i("CLIENT","In pre execute"); 
		    dialog = new ProgressDialog(MainActivity.this);
		    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    dialog.setMax(100);
		    dialog.setCancelable(true);
		}
		@Override
		protected Socket doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Socket socket = null;
			DataInputStream dataInputStream = null;
			DataOutputStream dataOutputStream = null;
			Context con = null;
			ServerSocket serverSocket = null;
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
					Log.i("SERVER","Address:"+socket.getInetAddress());
					int request = dataInputStream.readInt();
					if(request==10)
					{
						SharedPreferences mPrefs;
		       		    mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		       		    String Nick = mPrefs.getString("Nick", "");
						dataOutputStream.writeUTF(Nick);
						socket.close();
						dataInputStream.close();
						dataOutputStream.close();
					}
					else if(request==01){
					final String filename = dataInputStream.readUTF();
					final String address = socket.getInetAddress().toString();
					//dataOutputStream.writeUTF("ACK");
					runOnUiThread(new Runnable() {
						public void run() {
							DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							    @Override
							    public void onClick(DialogInterface dialog, int which) {
							        switch (which){
							        case DialogInterface.BUTTON_POSITIVE:
							            {
							            	MainActivity.this.concurrent=1;
							            	break;
							            }

							        case DialogInterface.BUTTON_NEGATIVE:
							        {
							            MainActivity.this.concurrent=2;
							            break;
							        }
							        }
							    }
							};
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setMessage(address.substring(1)+" wants to share "+filename+" with you?").setPositiveButton("Accept", dialogClickListener)
							    .setNegativeButton("Decline", dialogClickListener).setCancelable(false).show();
						}
					});
					while(MainActivity.this.concurrent==0)
					{
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					switch (MainActivity.this.concurrent) {
						case 1:
						{
							runOnUiThread(new Runnable() {
								public void run() {
									
								    dialog.setMessage("Receiving "+filename+"...");
									dialog.show();
								}
							});
							dataOutputStream.writeUTF("ACK");
							Log.i("SERVER","In the zone");
							Log.i("SERVER","Socket closed:"+socket.isClosed());
							int filesize=dataInputStream.readInt();
							Log.i("SERVER","filesize length:"+filesize);
							 byte [] mybytearray  = new byte [32768];
							 File folder = new File(Environment.getExternalStorageDirectory() + "//shared");
							 folder.mkdir();
							 FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"//Shared//"+filename);
							 //for(int i=0;i<filesize;i++)
						 	 //{
							 	long before, after;
							 	before = System.currentTimeMillis();
							 	int total=0,count=0;
							    while ((count = dataInputStream.read(mybytearray)) > 0) {
							        fos.write(mybytearray, 0, count);
							        total=total+count;
							        publishProgress(total,filesize);
							    }
							 	after = System.currentTimeMillis();
							 	Log.i("SERVER","Received"+new File(Environment.getExternalStorageDirectory()+"//shared//"+filename).length()+", Time taken:"+(after-before));
							 	Log.i("SERVER",mybytearray[0]+", "+mybytearray[1]+", "+mybytearray[2]);
							 	runOnUiThread(new Runnable() {
									public void run() {
										Log.i("SERVER","Inside UI thread");
										Toast.makeText(getApplicationContext(), "File "+filename+" is stored in shared/", 3000).show();
									}
							 });
								runOnUiThread(new Runnable() {
									public void run() {
										dialog.dismiss();
									}
								});
							 	socket.close();
							 	fos.close();
							break;
						}
						case 2:
						{
							dataOutputStream.writeUTF("DRP");
							break;
						}
					}
					MainActivity.this.concurrent=0;
					socket.close();
					//socket.getInetAddress()+" wants to share "+filename+" with you?
					}
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
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		    	//Log.i("CLIENT","Inside publishProg "+values[1]+", "+progress[0]+", "+fprogress*100);
		    	int sProgress = (int)(((double)values[0]/(double)values[1]) * 100);;
		    	//Log.i("CLIENT","YE:"+sProgress);
		        dialog.setProgress(sProgress);
		}
		@Override
		protected void onPostExecute(Socket result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}

}
