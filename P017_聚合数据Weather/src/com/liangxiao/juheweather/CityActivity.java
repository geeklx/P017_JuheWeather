package com.liangxiao.juheweather;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liangxiao.juheweather.adapter.CityListAdapter;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.weather.WeatherData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CityActivity extends Activity {
	private ListView lv_city;
	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		initView();
		getCities();
	}

	private void initView() {
		findViewById(R.id.iv_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						finish();
					}
				});
		lv_city = (ListView) findViewById(R.id.lv_city);
	}

	private void getCities() {
		WeatherData data = WeatherData.getInstance();
		data.getCities(new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject json) {
				System.out.println(json.toString());
				try {
					int code = json.getInt("resultcode");
					int error_code = json.getInt("error_code");
					if (code == 200 && error_code == 0) {
						list = new ArrayList<String>();

						JSONArray resultJson = json.getJSONArray("result");
						Set<String> citySet = new HashSet<String>();
						for (int i = 0; i < resultJson.length(); i++) {
							// JSONObject jsonObject =
							// resultJson.getJSONObject(i);
							String city = resultJson.getJSONObject(i)
									.getString("city");
							citySet.add(city);
						}
						list.addAll(citySet);// 数据在list中
						CityListAdapter adapter = new CityListAdapter(
								CityActivity.this, list);
						lv_city.setAdapter(adapter);
						lv_city.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Intent intent = new Intent();
								intent.putExtra("city", list.get(position));
								setResult(1, intent);
								finish();
							}
						});
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
}
