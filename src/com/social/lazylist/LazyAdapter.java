package com.social.lazylist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

import com.social.bubbles.Bubble;
import com.social.bubbles.BubbleLauncher;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private LinkedList<Bubble> data = new LinkedList<Bubble>();
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a) {
        activity = a;
        data = BubbleManager.getSortedCacheList();
        Log.d("LazyAdapter", "data obtained Cache List from BM with size: " +data.size());
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public static class ViewHolder{
        public TextView text;
        public TextView location;
        public TextView addr;

        public ImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.item, null);
            holder=new ViewHolder();
            holder.text=(TextView)vi.findViewById(R.id.text);
            holder.location=(TextView)vi.findViewById(R.id.location);
            holder.addr=(TextView)vi.findViewById(R.id.addr);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();
        
        if(data.get(position).getDescription() == null){
        	Log.d("WTF description is null ID=", data.get(position).getId());
        }
        int upper = Math.min(45, data.get(position).getDescription().length());

        holder.text.setText(data.get(position).getDescription().substring(0, upper));
        holder.location.setText(data.get(position).getType());
        holder.addr.setText(data.get(position).getLastActivity().toString());

        holder.image.setTag(data.get(position).getDescription());
        //imageLoader.DisplayImage(data.get(position).picUrl, activity, holder.image);
        holder.image.setImageBitmap(BubbleLauncher.getImage(data.get(position).getId(), data.get(position).getType()));
        return vi;
    }
}