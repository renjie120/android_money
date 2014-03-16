package com.renjie.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.renjie.R;

public class MoneyNewAdapter extends BaseAdapter {
	private LinkedList<IMoneyData> data;// 用于接收传递过来的Context对象
	private Context context;

	public MoneyNewAdapter(LinkedList<IMoneyData> data, Context context) {
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
	public IMoneyData getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.money_list2, null);

			viewHolder.year = (TextView) convertView.findViewById(R.id.year);
			viewHolder.month = (TextView) convertView.findViewById(R.id.month);
			viewHolder.day = (TextView) convertView.findViewById(R.id.day);
			viewHolder.day = (TextView) convertView.findViewById(R.id.arrow);
			viewHolder.moneytype = (TextView) convertView
					.findViewById(R.id.moneytype);
			viewHolder.arrow = (TextView) convertView.findViewById(R.id.arrow);
			viewHolder.money = (TextView) convertView.findViewById(R.id.money);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		IMoneyData markerItem = getItem(position);
		if (null != markerItem) {
			if ("year".equals(markerItem.getLevel())) {
				viewHolder.year.setVisibility(View.VISIBLE);
				viewHolder.month.setVisibility(View.GONE);
				viewHolder.day.setVisibility(View.GONE);
				viewHolder.year.setText(markerItem.getTime() + "年");
				viewHolder.year.setTag(markerItem.getTime());
			} else if ("month".equals(markerItem.getLevel())) {
				String[] temp = markerItem.getTime().split(",");
				viewHolder.year.setTag(temp[0]);
				viewHolder.year.setVisibility(View.GONE);
				viewHolder.month.setVisibility(View.VISIBLE);
				viewHolder.day.setVisibility(View.GONE);
				viewHolder.month.setText(temp[1] + "月");
				viewHolder.month.setTag(temp[1]);
			} else if ("day".equals(markerItem.getLevel())) {
				String[] temp = markerItem.getTime().split(",");
				viewHolder.year.setTag(temp[0]);
				viewHolder.year.setVisibility(View.GONE);
				viewHolder.month.setTag(temp[1]);
				viewHolder.month.setVisibility(View.GONE);
				viewHolder.day.setVisibility(View.VISIBLE);
				viewHolder.day.setText(temp[2] + "日");
				viewHolder.day.setTag(markerItem.getTime());
			}
			if (!"day".equals(markerItem.getLevel())) {
				if ("true".equals(markerItem.getIsClosed())) {
					viewHolder.arrow.setText("展开");
					viewHolder.arrow.setTag("closed");
				} else {
					viewHolder.arrow.setText("收缩");
					viewHolder.arrow.setTag("opened");
				}
				viewHolder.arrow.setVisibility(View.VISIBLE);
			} else {
				viewHolder.arrow.setVisibility(View.GONE);
			}
			viewHolder.moneytype.setText(markerItem.getMoneyType());
			viewHolder.money.setText("" + markerItem.getMoney());
			viewHolder.money.setTag("" + markerItem.getLevel());
		}
		return convertView;
	}

	public final static class ViewHolder {
		public TextView year;
		public TextView month;
		public TextView day;
		public TextView moneytype;
		public TextView money;
		public TextView arrow;
	}

}
