package com.example.qq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final int VIEW_TYPE = 0xb01;
	private final int VIEW_TYPE_LEFT = -10;
	private final int VIEW_TYPE_RIGHT = -11;

	private final int MESSAGE = 0xb02;

	private MyAdapter adapter;
	private ListView listView;
	private int count;
	private ArrayList<HashMap<Integer, Object>> items = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		listView = (ListView) findViewById(R.id.list);
		items = new ArrayList<HashMap<Integer,Object>>();
		
		adapter = new MyAdapter(this,-1);
		listView.setAdapter(adapter);
		
		final EditText msgEditText = (EditText) findViewById(R.id.msgEditText);
		Button button = (Button) findViewById(R.id.msgSend);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = msgEditText.getText()+"";
				sendMsg(VIEW_TYPE_RIGHT,msg);
				
				// new MyTask().execute();

				// adapter.notifyDataSetChanged();

				// 发送后清空输入框内容
				msgEditText.setText(null);
			}
		});
		
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				sendMsg(VIEW_TYPE_LEFT,msg.obj+"");
			}	
		};
		
		final Random rand = new Random();
		new Thread(new Runnable(){

			@Override
			public void run() {
				for(int i=0;i<20;i++){
					try {
						Thread.sleep(1000*rand.nextInt(10));
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					Message msg = handler.obtainMessage();
					msg.obj=count++ +"";
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	private void sendMsg(int type, String msg) {
		HashMap<Integer,Object> map = new HashMap<Integer,Object>();
		map.put(VIEW_TYPE,type);
		map.put(MESSAGE, msg);
		items.add(map);
		
		adapter.notifyDataSetChanged();
		//输入框发送消息后将ListView滚动到最底部
		listView.setSelection(ListView.FOCUS_DOWN);	
	}

	private class MyAdapter extends ArrayAdapter {
		private LayoutInflater layoutInflater;

		public MyAdapter(Context context, int resource) {
			super(context, resource);
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
		int type = getItemViewType(pos);
		String msg = (String) getItem(pos);
		
		switch(type){
		case VIEW_TYPE_LEFT:
			convertView = layoutInflater.inflate(R.layout.left,null);
			TextView textLeft=(TextView) convertView
					.findViewById(R.id.textView);
			textLeft.setText(msg);
			break;
			
		case VIEW_TYPE_RIGHT:
			convertView = layoutInflater.inflate(R.layout.right, null);
			TextView textRight = (TextView) convertView.findViewById(R.id.textView);
			textRight.setText(msg);
			break;
		}
			return convertView;	
		}

		@Override
		public int getCount() {
			
			return items.size();
		}

		@Override
		public Object getItem(int pos) {
			HashMap<Integer, Object> map = items.get(pos);
			String s = map.get(MESSAGE) + "";
			return s;
		}
		
		@Override
		public int getItemViewType(int pos) {
			int type = (Integer) items.get(pos).get(VIEW_TYPE);
			return type;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
	}
}