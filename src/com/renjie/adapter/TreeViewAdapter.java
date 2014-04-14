package com.renjie.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.renjie.R;

public class TreeViewAdapter extends BaseAdapter {
	class ViewHolder {
		ImageView icon;
		TextView title;
	}

	Context context;
	ViewHolder holder;
	LayoutInflater inflater;
	List<TreeElement> elements;

	public TreeViewAdapter(Context context, List<TreeElement> elements) {
		this.context = context;
		this.elements = elements;
	}

	@Override
	public int getCount() {
		return elements.size();
	}

	@Override
	public Object getItem(int position) {
		return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (inflater == null) {
				inflater = LayoutInflater.from(context);
			}
			holder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.tree_view_item_layout, null);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.tree_view_item_icon);
			holder.title = (TextView) convertView
					.findViewById(R.id.tree_view_item_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (elements.get(position).isHasChild()) {// 有子节点，要显示图标
			if (elements.get(position).isFold()) {
				holder.icon.setImageResource(R.drawable.open);
			} else if (!elements.get(position).isFold()) {
				holder.icon.setImageResource(R.drawable.close);
			}
			holder.icon.setVisibility(View.VISIBLE);
		} else {// 没有子节点，要隐藏图标
			holder.icon.setImageResource(R.drawable.open);
			holder.icon.setVisibility(View.INVISIBLE);
		}
		holder.icon.setPadding(25 * (elements.get(position).getLevel()), 0, 0,
				0);// 根据层级设置缩进
		holder.title.setText(elements.get(position).getTitle());
		holder.title.setTextSize(40 - elements.get(position).getLevel() * 5); // 根据层级设置字体大小
		return convertView;
	}
}