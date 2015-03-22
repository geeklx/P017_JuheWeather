package com.liangxiao.juheweather.adapter;

import java.util.List;

import com.liangxiao.juheweather.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityListAdapter extends BaseAdapter {
	private List<String> list;
	private LayoutInflater mInflater;

	public CityListAdapter(Context context, List<String> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.item, null);
		} else {
			view = convertView;
		}

		TextView tv_city = (TextView) view.findViewById(R.id.tv_city);
		tv_city.setText(list.get(position) + "");

		return view;
	}

}
