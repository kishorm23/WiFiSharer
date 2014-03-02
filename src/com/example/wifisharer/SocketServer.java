package com.example.wifisharer;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.util.Enumeration;

import android.content.Context;
import android.util.Log;

public class SocketServer {
	public Socket socket;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	Context con;
	public SocketServer(Context context)
	{
		socket=null;
		dataInputStream=null;
		dataOutputStream=null;
		con=context;
	}
	public String Listen() throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(9999);
		Log.i("SERVER","Listening on "+getLocalIpAddress()+" 9999...");
		while(true)
		{
			try
			{
				socket = serverSocket.accept();
				//dataInputStream = new DataInputStream(socket.getInputStream());
				//dataOutputStream = new DataOutputStream(socket.getOutputStream());
				Log.i("SERVER","Address:"+socket.getInetAddress());
				//String filename = dataInputStream.readUTF();
				//dataOutputStream.writeUTF("Hello World");
				if(socket.isClosed())
					Log.i("SOCKET","Closed");
					else Log.i("SOCKET","Not closed");
				
				return "";
				
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
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("SERVER", ex.toString());
	    }
	    return null;
	}
}
