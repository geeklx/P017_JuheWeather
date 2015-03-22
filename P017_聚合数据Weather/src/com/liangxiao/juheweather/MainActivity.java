package com.liangxiao.juheweather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract.Contacts.Data;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.liangxiao.juheweather.bean.FutureWeatherBean;
import com.liangxiao.juheweather.bean.HourWeatherBean;
import com.liangxiao.juheweather.bean.PMBean;
import com.liangxiao.juheweather.bean.WeatherBean;
import com.liangxiao.juheweather.service.WeatherService;
import com.liangxiao.juheweather.service.WeatherService.OnParserCallBack;
import com.liangxiao.juheweather.service.WeatherService.WeatherServiceBinder;
import com.liangxiao.juheweather.swiperefresh.PullToRefreshBase;
import com.liangxiao.juheweather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.liangxiao.juheweather.swiperefresh.PullToRefreshScrollView;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

public class MainActivity extends Activity {
	private Context mContext;
	private WeatherService mService;
	private PullToRefreshScrollView mPullPullToRefreshScrollView;
	private ScrollView mScrollView;

	private TextView tv_city,// 城市
			tv_release,// 更新时间
			tv_now_weather,// 天气
			tv_today_temp,// 温度
			tv_now_temp,// 当前温度
			tv_aqi,// 空气质量指数
			tv_quality,// 空气质量
			tv_next_three,// 3小时
			tv_next_six,// 6小时
			tv_next_nine,// 9小时
			tv_next_twelve,// 12小时
			tv_next_fifteen,// 15小时
			tv_next_three_temp,// 3小时温度
			tv_next_six_temp,// 6小时温度
			tv_next_nine_temp,// 9小时温度
			tv_next_twelve_temp,// 12小时温度
			tv_next_fifteen_temp,// 15小时温度
			tv_today_temp_a,// 今天温度a
			tv_today_temp_b,// 今天温度b
			tv_tommorrow,// 明天
			tv_tommorrow_temp_a,// 明天温度a
			tv_tommorrow_temp_b,// 明天温度b
			tv_thirdday,// 第三天
			tv_thirdday_temp_a,// 第三天温度a
			tv_thirdday_temp_b,// 第三天温度b
			tv_fourthday,// 第四天
			tv_fourthday_temp_a,// 第四天温度a
			tv_fourthday_temp_b,// 第四天温度b
			tv_felt_air_temp,// 体感温度
			tv_humidity,// 湿度
			tv_wind,// 风向与风力
			tv_uv_index,// 紫外线
			tv_dressing_index;// 穿衣指数
	private ImageView iv_now_weather,// 有雾
			iv_next_three,// 3小时
			iv_next_six,// 6小时
			iv_next_nine,// 9小时
			iv_next_twelve,// 12小时
			iv_next_fifteen,// 15小时
			iv_today_weather,// 今天
			iv_tommorrow_weather,// 明天
			iv_thirdday_weather,// 第三天
			iv_fourthday_weather;// 第四天
	private RelativeLayout rl_city;
	private Boolean isRunning = false;
	private int count = 0;
	private String citys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// getCityWeather();
		mContext = this;
		init();
		// 调用WeatherService部分
		initService();
	}

	private void initService() {
		Intent intent = new Intent(mContext, WeatherService.class);
		startService(intent);
		// 绑定WeatherService部分
		bindService(intent, conn, Context.BIND_AUTO_CREATE);

	}

	// 连接绑定WeatherService部分
	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mService.removeCallBack();
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			mService = ((WeatherServiceBinder) arg1).getService();
			mService.test();
			// 服务连接的时候回调OnParserComplete方法传参绑定到UI
			mService.setCallBack(new OnParserCallBack() {

				@Override
				public void OnParserComplete(List<HourWeatherBean> hourlist,
						PMBean pmBean, WeatherBean weatherBean) {
					mPullPullToRefreshScrollView.onRefreshComplete();
					if (hourlist != null && hourlist.size() >= 5) {
						setHourViews3h(hourlist);
					}
					if (pmBean != null) {
						setPMViews(pmBean);
					}
					if (weatherBean != null) {
						setWeatherViews(weatherBean);
					}
				}
			});
			mService.getCityWeather();
		}
	};

	private void init() {
		// 下拉刷新部分
		mPullPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullPullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// getCityWeather();
						mService.getCityWeather();
						Toast.makeText(getApplicationContext(), "下拉刷新", 2)
								.show();
					}

				});

		mScrollView = mPullPullToRefreshScrollView.getRefreshableView();

		rl_city = (RelativeLayout) findViewById(R.id.rl_city);
		rl_city.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(
						new Intent(mContext, CityActivity.class), 1);
			}
		});
		tv_city = (TextView) findViewById(R.id.tv_city);// 城市
		tv_release = (TextView) findViewById(R.id.tv_release);// 更新时间
		tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);// 天气
		tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);// 温度
		tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);// 当前温度
		tv_aqi = (TextView) findViewById(R.id.tv_aqi);// 空气质量指数
		tv_quality = (TextView) findViewById(R.id.tv_quality);// 空气质量
		tv_next_three = (TextView) findViewById(R.id.tv_next_three);// 3小时
		tv_next_six = (TextView) findViewById(R.id.tv_next_six);// 6小时
		tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);// 9小时
		tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);// 12小时
		tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);// 15小时
		tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);// 3小时温度
		tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);// 6小时温度
		tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);// 9小时温度
		tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);// 12小时温度
		tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);// 15小时温度
		tv_today_temp_a = (TextView) findViewById(R.id.tv_today_temp_a);// 今天温度a
		tv_today_temp_b = (TextView) findViewById(R.id.tv_today_temp_b);// 今天温度b
		tv_tommorrow = (TextView) findViewById(R.id.tv_tommorrow);// 明天
		tv_tommorrow_temp_a = (TextView) findViewById(R.id.tv_tommorrow_temp_a);// 明天温度a
		tv_tommorrow_temp_b = (TextView) findViewById(R.id.tv_tommorrow_temp_b);// 明天温度b
		tv_thirdday = (TextView) findViewById(R.id.tv_thirdday);// 第三天
		tv_thirdday_temp_a = (TextView) findViewById(R.id.tv_thirdday_temp_a);// 第三天温度a
		tv_thirdday_temp_b = (TextView) findViewById(R.id.tv_thirdday_temp_b);// 第三天温度b
		tv_fourthday = (TextView) findViewById(R.id.tv_fourthday);// 第四天
		tv_fourthday_temp_a = (TextView) findViewById(R.id.tv_fourthday_temp_a);// 第四天温度a
		tv_fourthday_temp_b = (TextView) findViewById(R.id.tv_fourthday_temp_b);// 第四天温度b
		tv_felt_air_temp = (TextView) findViewById(R.id.tv_felt_air_temp);// 体感温度
		tv_humidity = (TextView) findViewById(R.id.tv_humidity);// 湿度
		tv_wind = (TextView) findViewById(R.id.tv_wind);// 风向与风力
		tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);// 紫外线
		tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);// 穿衣指数

		iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);// 有雾
		iv_next_three = (ImageView) findViewById(R.id.iv_next_three);// 3小时
		iv_next_six = (ImageView) findViewById(R.id.iv_next_six);// 6小时
		iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);// 9小时
		iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);// 12小时
		iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);// 15小时
		iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);// 今天
		iv_tommorrow_weather = (ImageView) findViewById(R.id.iv_tommorrow_weather);// 明天
		iv_thirdday_weather = (ImageView) findViewById(R.id.iv_thirdday_weather);// 第三天
		iv_fourthday_weather = (ImageView) findViewById(R.id.iv_fourthday_weather);// 第四天
	}

	/**
	 * PM2.5给控件填充数据部分
	 * 
	 * @param bean
	 */
	private void setPMViews(PMBean bean) {
		tv_aqi.setText(bean.getAqi());// 空气质量指数
		tv_quality.setText(bean.getQuality());// 空气质量
	}

	/**
	 * 三小时城市天气给控件填充数据部分
	 * 
	 * @param list
	 */
	private void setHourViews3h(List<HourWeatherBean> list) {
		if (list != null && list.size() == 5) {
			setHourData(tv_next_three, iv_next_three, tv_next_three_temp,
					list.get(0));
			setHourData(tv_next_six, iv_next_six, tv_next_six_temp, list.get(1));
			setHourData(tv_next_nine, iv_next_nine, tv_next_nine_temp,
					list.get(2));
			setHourData(tv_next_twelve, iv_next_twelve, tv_next_twelve_temp,
					list.get(3));
			setHourData(tv_next_fifteen, iv_next_fifteen, tv_next_fifteen_temp,
					list.get(4));

		}
	}

	/**
	 * 城市天气给控件填充数据部分
	 * 
	 * @param bean
	 */
	private void setWeatherViews(WeatherBean bean) {

		tv_city.setText(bean.getCity());// 城市
		tv_release.setText(bean.getRelase());// 更新时间
		tv_now_weather.setText(bean.getWeather_str());// 天气
		String[] tempArr = bean.getTemp().split("~");// 9℃和11℃ °
		String temp_str_a = tempArr[0].substring(0, tempArr[0].indexOf("℃"));// 9
		String temp_str_b = tempArr[1].substring(0, tempArr[1].indexOf("℃"));// 11
		String temp_ab = "↑ " + temp_str_b + " " + " ↓" + temp_str_a;
		tv_today_temp.setText(temp_ab);// 温度9℃~11℃
		tv_now_temp.setText(bean.getNow_temp() + "°");// 当前温度
		iv_today_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable",
				"com.liangxiao.juheweather"));
		// 今天
		tv_today_temp_a.setText(temp_str_a + "°");// 今天温度a
		tv_today_temp_b.setText(temp_str_b + "°");// 今天温度b

		List<FutureWeatherBean> futureList = bean.getFutureList();
		if (futureList != null && futureList.size() == 3) {
			setFutureData(tv_tommorrow, iv_tommorrow_weather,
					tv_tommorrow_temp_a, tv_tommorrow_temp_b, futureList.get(0));
			setFutureData(tv_thirdday, iv_thirdday_weather, tv_thirdday_temp_a,
					tv_thirdday_temp_b, futureList.get(1));
			setFutureData(tv_fourthday, iv_fourthday_weather,
					tv_fourthday_temp_a, tv_fourthday_temp_b, futureList.get(2));
		}
		Calendar c = Calendar.getInstance();
		String prefixStr = null;
		int time = c.get(Calendar.HOUR_OF_DAY);
		if (time >= 6 && time < 18) {
			prefixStr = "d";// 白天6~18
		} else {
			prefixStr = "n";// 夜晚18~24~06
		}
		iv_now_weather.setImageResource(getResources().getIdentifier(
				prefixStr + bean.getWeather_id(), "drawable",
				"com.liangxiao.juheweather"));
		tv_felt_air_temp.setText(bean.getFelt_temp() + "°");// 体感温度
		tv_humidity.setText(bean.getHumidity());// 湿度
		tv_wind.setText(bean.getWind());// 风向与风力
		tv_uv_index.setText(bean.getUv_index());// 紫外线
		tv_dressing_index.setText(bean.getDressing_index());// 穿衣指数
	}

	/**
	 * 天气预报填充数据到控件的类部分
	 * 
	 * @param tv_week
	 *            哪一天
	 * @param iv_weather
	 *            有雾
	 * @param tv_temp_a
	 *            最高温度
	 * @param tv_temp_b
	 *            最低温度
	 * @param bean
	 *            实例化FutureWeatherBean对象
	 */
	private void setFutureData(TextView tv_week, ImageView iv_weather,
			TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean bean) {
		tv_week.setText(bean.getWeek());// 第二天
		iv_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable",
				"com.liangxiao.juheweather"));// 有雾图片
		String[] tempArr = bean.getTemperature().split("~");
		String tempArr_a = tempArr[0].substring(0, tempArr[0].indexOf("℃"));
		String tempArr_b = tempArr[1].substring(0, tempArr[1].indexOf("℃"));
		tv_temp_a.setText(tempArr_a + "°");
		tv_temp_b.setText(tempArr_b + "°");

	}

	/**
	 * 三小时天气填充数据到控件的类的部分
	 * 
	 * @param tv_next
	 * @param iv_next
	 * @param tv_next_temp
	 * @param bean
	 */
	private void setHourData(TextView tv_next, ImageView iv_next,
			TextView tv_next_temp, HourWeatherBean bean) {
		String prefixStr = null;
		int time = Integer.valueOf(bean.getTime());// 05
		if (time >= 6 && time < 18) {
			prefixStr = "d";// 白天6~18
		} else {
			prefixStr = "n";// 夜晚18~24~06
		}

		// if(){
		//
		// }

		tv_next.setText(bean.getTime()+"时");
		iv_next.setImageResource(getResources().getIdentifier(
				prefixStr + bean.getWeather_id(), "drawable",
				"com.liangxiao.juheweather"));
		tv_next_temp.setText(bean.getTemp() + "°");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 1) {
			citys = data.getStringExtra("city");
			mService.getCityWeather(citys);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		// 断开连接
		unbindService(conn);
		super.onDestroy();

	}
}
