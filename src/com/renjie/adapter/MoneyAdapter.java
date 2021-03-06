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

public class MoneyAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> data;// 用于接收传递过来的Context对象
	private Context context;
	private boolean isSuper;

	public MoneyAdapter(ArrayList<HashMap<String, Object>> data,boolean isSuper, Context context) {
		super();
		this.data = data;
		this.isSuper = isSuper;
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
			convertView = mInflater.inflate(R.layout.money_list, null);

			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.moneytype = (TextView) convertView
					.findViewById(R.id.moneytype);
			viewHolder.money = (TextView) convertView.findViewById(R.id.money);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, Object> markerItem = getItem(position);
		if (null != markerItem) {
			viewHolder.time.setTag(markerItem.get("sno"));
			viewHolder.time.setText("" + markerItem.get("time"));
			String tpNm = "" + markerItem.get("moneytype");
			viewHolder.moneytype.setText(tpNm);
			//如果是收入类型就进行隐藏.
			if(Tool.isInType(tpNm)&&!isSuper)
				viewHolder.money.setText("*");
			else
				viewHolder.money.setText("" + markerItem.get("money"));
		}
		return convertView;
	}

	public final static class ViewHolder {
		public TextView time;
		public TextView moneytype;
		public TextView money;
	}

}
