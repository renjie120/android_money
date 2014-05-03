package com.renjie.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.renjie.Node;
import com.renjie.R;

public class MoneyList2Adatper extends BaseAdapter {
	private List<Node> data;// 用于接收传递过来的Context对象
	private Context context;
	private boolean showNext, showSpan, showDesc;

	/**
	 * 
	 * @param data
	 *            数据
	 * @param context
	 * @param showNext
	 *            是否显示下一级别
	 * @param showSpan
	 *            是否显示首页
	 * @param showDesc
	 *            是否显示描述信息.
	 */
	public MoneyList2Adatper(List<Node> data, Context context,
			boolean showNext, boolean showSpan, boolean showDesc) {
		super();
		this.data = data;
		this.context = context;
		this.showSpan = showSpan;
		this.showNext = showNext;
		this.showDesc = showDesc;
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
	public Node getItem(int position) {
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
			convertView = mInflater
					.inflate(R.layout.list_money_item2, null);

			viewHolder.time = (TextView) convertView
					.findViewById(R.id.time);
			viewHolder.money = (TextView) convertView
					.findViewById(R.id.money);
			viewHolder.next = (ImageView) convertView
					.findViewById(R.id.next);
			viewHolder.span = (TextView) convertView
					.findViewById(R.id.span);
			viewHolder.desc = (TextView) convertView
					.findViewById(R.id.m_desc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (showNext)
			viewHolder.next.setVisibility(View.VISIBLE);
		else
			viewHolder.next.setVisibility(View.GONE);

		if (showSpan)
			viewHolder.span.setVisibility(View.VISIBLE);
		else
			viewHolder.span.setVisibility(View.GONE);

		viewHolder.desc.setVisibility(View.GONE);

		Node markerItem = getItem(position);
		if (null != markerItem) {
			if (showSpan) {
				String[] s = markerItem.getName().split(",");
				viewHolder.time.setText(s[0]);
				viewHolder.span.setText(s[1]);
			} else {
				viewHolder.time.setText(markerItem.getName());
			}
			viewHolder.money.setText(markerItem.getCode());
			viewHolder.money.setTag(markerItem.getLevel());
			viewHolder.next.setTag(markerItem.getId());
			String str = markerItem.getParam1();
			if (str != null && !"null".equals(str) && !"".equals(str)) {
				viewHolder.desc.setVisibility(View.VISIBLE);
				viewHolder.desc.setText(str);
			} else
				viewHolder.desc.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		// 时间
		TextView time;
		// 是否出现打开下一级别的图标
		ImageView next;
		// 金额
		TextView money;
		TextView span;
		// 描述信息
		TextView desc;
	}

}