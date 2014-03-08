package com.example.wifisharer;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {

private Context context;
private ArrayList<String> allHost;
private ArrayList<String> allIp;
private LayoutInflater mInflater;
private boolean mNotifyOnChange = true;

public MyListAdapter(Context context, ArrayList<String> mHost, ArrayList<String> mIp) {
    super(context, R.layout.listview_layout);
    this.context = context;
    this.allHost = new ArrayList<String>(mHost);
    this.allIp = new ArrayList<String>(mIp);
    this.mInflater = LayoutInflater.from(context);
}

@Override
public int getCount() {
    return allHost.size();
}

public String getHostName(int position) {
    return allHost.get(position);
}

public String getHostIp(int position) {
    return allIp.get(position);
}

@Override
public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
}

public int getHostPosition(String item) {
    return allHost.indexOf(item);
}
public int getIpPosition(String item) {
    return allIp.indexOf(item);
}
@Override
public int getViewTypeCount() {
    return 1; //Number of types + 1 !!!!!!!!
}

@Override
public int getItemViewType(int position) {
    return 1;
}


@Override
public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;
    int type = getItemViewType(position);
    if (convertView == null) {
        holder = new ViewHolder();
        switch (type) {
        case 1:
            convertView = mInflater.inflate(R.layout.listview_layout,parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.firstLine);
            holder.description = (TextView) convertView.findViewById(R.id.secondLine);
            break;
        }
        convertView.setTag(holder);
    } else {
        holder = (ViewHolder) convertView.getTag();
    }
    holder.name.setText(allHost.get(position));
    holder.description.setText(allIp.get(position));
    holder.pos = position;
    return convertView;
}

@Override
public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    mNotifyOnChange = true;
}

public void setNotifyOnChange(boolean notifyOnChange) {
    mNotifyOnChange = notifyOnChange;
}


//---------------static views for each row-----------//
     static class ViewHolder {

         TextView name;
         TextView description;
         int pos; //to store the position of the item within the list
     }
}