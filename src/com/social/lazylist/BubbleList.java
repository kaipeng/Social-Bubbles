package com.social.lazylist;

import java.util.LinkedList;


import com.bubbles.location.MyLocation;
import com.social.bubbles.Bubble;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.DownloadManager;
import com.social.bubbles.R;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class BubbleList extends Activity {
    
    ListView list;
    LazyAdapter adapter;
    Activity mActivity;
    protected static Handler updateHandler;
    protected LinkedList<Bubble> bubbleList = new LinkedList<Bubble> ();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bubblelist);
        
        mActivity = this;
        list=(ListView)findViewById(R.id.list);
        adapter=new LazyAdapter(mActivity);
        list.setAdapter(adapter);
        
        Button b=(Button)findViewById(R.id.button1);
        b.setOnClickListener(listener);
        Button b2=(Button)findViewById(R.id.button2);
        b2.setOnClickListener(listener2);
        
		//updateHandler = new UpdateHandler();
		DownloadManager.setHandler(updateHandler);
    }
    
//    public class UpdateHandler extends Handler {
//		private static final int BUBBLE_UPDATE = 1;
//		public void handleMessage(Message msg) {
//        	Log.d("           RECEIVED MESSAGE TO UPDATE UI : FIRST IS ", (((LinkedList<Bubble>)msg.obj)).getFirst().title);
//    
//			switch (msg.what) {
//                case BUBBLE_UPDATE:
//                	Log.d("           RECEIVED MESSAGE TO UPDATE UI : FIRST IS ", (((LinkedList<Bubble>)msg.obj)).getFirst().title);
//                	adapter=new LazyAdapter(mActivity);
//                    list.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//  	          }
//        }
//    }
//			    
    
    @Override
    public void onDestroy()
    {
        adapter.imageLoader.stopThread();
        list.setAdapter(null);
        super.onDestroy();
    }
    
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
        	BubbleManager.queueQualifyingBubbles();
        	adapter=new LazyAdapter(mActivity);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };
    
    public OnClickListener listener2=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
        	Toast toast = Toast.makeText(mActivity, "Choose '<Social Bubbles Wallpaper>' from the list to start the Live Wallpaper.",Toast.LENGTH_LONG);
        	toast.show();
        	
        	Intent intent = new Intent();
        	intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        	startActivity(intent);
        }
    };
    
}