package com.example.wifisharer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.example.wifisharer.MainActivity.ClientSide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ScanActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		final EditText start = (EditText) findViewById(R.id.etStart);
		final EditText end = (EditText) findViewById(R.id.etEnd);
		final Pattern PARTIAl_IP_ADDRESS =
                Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                                 "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$"); 
		start.addTextChangedListener(new TextWatcher() {                       
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
	end.addTextChangedListener(new TextWatcher() {                       
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
        
        Button bScan = (Button) findViewById(R.id.scan);
        bScan.setOnClickListener(new OnClickListener() {
			
     			@SuppressLint("NewApi")
     			@Override
     			public void onClick(View v) {
     				new ScanHosts().execute(start.getText().toString(),end.getText().toString());
     				
     				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    				    new ScanHosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,start.getText().toString(),end.getText().toString());
    				 else
    				     new ScanHosts().execute(start.getText().toString(),end.getText().toString());
     				
     			}
     		});
	}
	
	class ScanHosts extends AsyncTask<String, Integer, Integer>
	{
		Socket socket = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		String[][] alive = new String[1000][2];
		ArrayList<String> aliveHostsIp = new ArrayList<String>();
		ArrayList<String> aliveHostName = new ArrayList<String>();
		@Override
		protected Integer doInBackground(String... params) {
			Log.i("SCAN","Inside background task");
			IPAddress start = new IPAddress(params[0]);
			IPAddress end = new IPAddress(params[1]);
			int iter = 0;
			for(IPAddress i=start;i.getValue()<=end.getValue();i=i.next())
			{
				Log.i("SCAN","Testing IP:"+i.toString());
				try {
					String IP = i.toString();
					socket = new Socket();
					socket.connect(new InetSocketAddress(IP, 9999), 100);
					dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataInputStream = new DataInputStream(socket.getInputStream());
					dataOutputStream.writeInt(10);
					String response = null;
					response = dataInputStream.readUTF();
					//if(response!=null)
					{
						Log.i("SCAN",IP+" is alive, name is "+response);
						alive[iter][0]=i.toString();
						alive[iter][1]=response;
						iter++;
					}
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(int j=0;j<iter;j++)
			{
				aliveHostsIp.add(alive[j][0]);
				aliveHostName.add(alive[j][1]);
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			Log.i("SCAN","Inside post execute");
			super.onPostExecute(result);
			 ListView lv = (ListView) findViewById(R.id.listview);
			 ArrayAdapter<String> arrayAdapterName = new ArrayAdapter<String>(
	                 ScanActivity.this, 
	                 R.layout.listview_layout,
	                 R.id.firstLine,
	                 aliveHostName );
			 ArrayAdapter<String> arrayAdapterIp = new ArrayAdapter<String>(
	                 ScanActivity.this, 
	                 R.layout.listview_layout,
	                 R.id.secondLine,
	                 aliveHostsIp );
			 MyListAdapter maAdapter;
			 maAdapter = new MyListAdapter(ScanActivity.this, aliveHostName, aliveHostsIp);
			 lv.setAdapter(maAdapter);
		}
		
	}


}
