package com.renjie.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.renjie.R;
import com.renjie.tool.Tool;

public class DiaryAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> data;// 用于接收传递过来的Context对象
	private Context context;
	

	public DiaryAdapter(ArrayList<HashMap<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (null != data) {
			count = data.size();
		}
		return count;
	}

	@Override
	public HashMap<String, Object> getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.diary_item, null);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.type = (TextView) convertView.findViewById(R.id.tp);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, Object> markerItem = getItem(position);
		if (null != markerItem) {
			String content = markerItem.get("content") + "";
			viewHolder.time.setText(markerItem.get("time") + "  ("
					+ content.length() + ")");
			viewHolder.time.setTag(markerItem.get("sno"));
			viewHolder.date.setText("" + markerItem.get("date"));
			if ("true".equals(markerItem.get("jiami"))) {
				viewHolder.content.setText("已经加密.");
			} else
				viewHolder.content.setText(content);
			String tp = ""+markerItem.get("type");
			if("".equals(tp.trim())||"null".equals(tp)){
				tp = Tool.DIARY_TYPE_COMMON+"";
			}
			viewHolder.type.setText(Tool.DIARY_TYPES[Integer.parseInt(tp)]); 
		}
		return convertView;
	}

	public final static class ViewHolder {
		public TextView time;
		public TextView date;
		public TextView content;
		public TextView type;
	}

}
