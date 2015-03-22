package com.liangxiao.juheweather.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.liangxiao.juheweather.bean.FutureWeatherBean;
import com.liangxiao.juheweather.bean.HourWeatherBean;
import com.liangxiao.juheweather.bean.PMBean;
import com.liangxiao.juheweather.bean.WeatherBean;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

public class WeatherService extends Service {
	private String city;
	private final String TAG = "WeatherService";
	private WeatherServiceBinder binder = new WeatherServiceBinder();
	private Boolean isRunning = false;
	private int count = 0;
	private List<HourWeatherBean> hourlist;
	// private HourWeatherBean hourWeatherBean;
	private PMBean pmBean;
	private WeatherBean weatherBean;
	private OnParserCallBack onParserCallBack;
	private static final int REPEAT_MSG = 0x01;// handler传值部分
	private static final int REFRESH_TIMES = 30 * 60 * 1000;

	/**
	 * 回调接口部分
	 * 
	 * @author lan
	 * 
	 */
	public interface OnParserCallBack {
		public void OnParserComplete(List<HourWeatherBean> hourlist,
				PMBean pmBean, WeatherBean weatherBean);
	}

	/**
	 * 回调设置方法部分
	 * 
	 * @param onParserCallBack
	 */
	public void setCallBack(OnParserCallBack onParserCallBack) {
		this.onParserCallBack = onParserCallBack;
	}

	/**
	 * 回调移除函数部分
	 */
	public void removeCallBack() {
		onParserCallBack = null;
	}

	public void getCityWeather(String citys) {
		this.city = citys;
		getCityWeather();
	}

	/**
	 * 与activity通信部分
	 * 
	 * @author lan
	 * 
	 */
	public class WeatherServiceBinder extends Binder {
		public WeatherService getService() {
			return WeatherService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

	public void test() {
		Log.v(TAG, "test WeatherService");
	}

	@Override
	public void onCreate() {
		city = "重庆";
		mHandler.sendEmptyMessage(REPEAT_MSG);
		Log.v(TAG, "WeatherService onCreate");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v(TAG, "WeatherService onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v(TAG, "WeatherService onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == REPEAT_MSG) {
				// 加载数据部分
				getCityWeather();
				// 发送一个message
				// handleMessage(msg);
				sendEmptyMessageDelayed(REPEAT_MSG, REFRESH_TIMES);
				// Toast.makeText(getApplicationContext(), "我刷新了！", 2).show();

			}
		};
	};

	/**
	 * 服务器读取json后传递给回调函数的OnParserComplete方法
	 */
	public void getCityWeather() {
		if (isRunning) {
			return;
		}
		isRunning = true;
		Log.v("count", count + "");
		count = 0;

		// 天气预报
		WeatherData data = WeatherData.getInstance();

		data.getByCitys(city, 2, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				synchronized (this) {
					count++;
				}
				weatherBean = WeathergetByCitys(arg0);
				if (weatherBean != null) {
					// 设置控件内容部分
					// setWeatherViews(weatherBean);
				}

				if (count == 3) {
					// mPullPullToRefreshScrollView.onRefreshComplete();
					if (onParserCallBack != null) {
						// 设置控件内容部分
						onParserCallBack.OnParserComplete(hourlist, pmBean,
								weatherBean);
					}

					isRunning = false;
				}

			}
		});

		// 三小时天气
		data.getForecast3h(city, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				count++;
				hourlist = WeathergetForecast3h(arg0);
				if (hourlist != null && hourlist.size() >= 5) {
					// 设置控件内容部分
					// setHourViews3h(list);
				}
				if (count == 3) {
					// mPullPullToRefreshScrollView.onRefreshComplete();
					if (onParserCallBack != null) {
						// 设置控件内容部分
						onParserCallBack.OnParserComplete(hourlist, pmBean,
								weatherBean);
					}

					isRunning = false;
				}
			}
		});

		// PM2.5值
		AirData pmData = AirData.getInstance();
		pmData.cityAir(city, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				count++;
				pmBean = WeathercityAir(arg0);
				if (pmBean != null) {
					// 设置控件内容部分
					// setPMViews(bean);
				}
				if (count == 3) {
					// mPullPullToRefreshScrollView.onRefreshComplete();
					if (onParserCallBack != null) {
						// 设置控件内容部分
						onParserCallBack.OnParserComplete(hourlist, pmBean,
								weatherBean);
					}

					isRunning = false;
				}
			}
		});
	}

	/**
	 * 解析天气预报部分
	 * 
	 * @param json
	 * @return
	 */
	private WeatherBean WeathergetByCitys(JSONObject json) {
		WeatherBean bean = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			int resultcode = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (resultcode == 200 && error_code == 0) {
				JSONObject resultJson = json.getJSONObject("result");
				bean = new WeatherBean();

				// today
				JSONObject todayJson = resultJson.getJSONObject("today");// 今日天气
				bean.setCity(todayJson.getString("city"));// 城市
				bean.setUv_index(todayJson.getString("uv_index"));// 紫外线强度
				bean.setTemp(todayJson.getString("temperature"));// 今日温度
				bean.setWeather_str(todayJson.getString("weather"));// 今日天气
				bean.setWeather_id(todayJson.getJSONObject("weather_id")// 天气唯一标识
						.getString("fa"));// 天气标识00：晴
				bean.setDressing_index(todayJson.getString("dressing_index"));// 穿衣指数（较冷）

				// sk
				JSONObject skJson = resultJson.getJSONObject("sk");// 当前实况天气
				bean.setWind(skJson.getString("wind_direction")// 当前风向
						+ skJson.getString("wind_strength"));// 当前风力
				bean.setNow_temp(skJson.getString("temp"));// 当前温度
				bean.setRelase(skJson.getString("time") + "发布");// 更新时间
				bean.setHumidity(skJson.getString("humidity"));// 当前湿度
				bean.setFelt_temp(skJson.getString("temp"));

				// future
				Date date = new Date(System.currentTimeMillis());
				JSONArray futureArray = resultJson.getJSONArray("future");
				List<FutureWeatherBean> futureList = new ArrayList<FutureWeatherBean>();

				for (int i = 0; i < futureArray.length(); i++) {
					JSONObject futureJson = futureArray.getJSONObject(i);
					FutureWeatherBean Fbean = new FutureWeatherBean();

					Date datef = sdf.parse(futureJson.getString("date"));
					if (!datef.after(date))
						// 获取的时间早于当前时间不取值跳过
						continue;

					Fbean.setWeek(futureJson.getString("week"));
					Fbean.setWeather(futureJson.getString("weather"));
					Fbean.setTemperature(futureJson.getString("temperature"));
					Fbean.setWeather_id(futureJson.getJSONObject("weather_id")
							.getString("fa"));
					futureList.add(Fbean);
					if (futureList.size() == 3)
						break;
				}
				bean.setFutureList(futureList);
			} else
				Toast.makeText(getApplicationContext(), "天气预报未获取到数据",
						Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 解析三小时城市天气预报部分
	 * 
	 * @param json
	 * @return
	 */
	private List<HourWeatherBean> WeathergetForecast3h(JSONObject json) {

		List<HourWeatherBean> list = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date(System.currentTimeMillis());
		try {

			int error_code = json.getInt("error_code");
			int resultcode = json.getInt("resultcode");
			if (resultcode == 200 && error_code == 0) {

				// 存放数据的数组
				list = new ArrayList<HourWeatherBean>();
				// 获得数据JSONArray
				JSONArray resultArray = json.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					// 获得其中的i个对象
					JSONObject resultObject = resultArray.getJSONObject(i);
					// 实例化对象参数
					HourWeatherBean HBean = new HourWeatherBean();
					Date dateh = sdf.parse(resultObject.getString("sfdate"));
					if (!dateh.after(date)) {
						// 获取的时间早于当前时间不取值跳过
						continue;
					}
					Calendar c = Calendar.getInstance();
					c.setTime(dateh);
					HBean.setTime(c.get(Calendar.HOUR_OF_DAY) + "");
					HBean.setWeather_id(resultObject.getString("weatherid"));
					HBean.setTemp(resultObject.getString("temp1"));

					list.add(HBean);

					if (list.size() == 5) {
						break;
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "三小时城市天气未获取到数据",
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 解析PM2.5部分
	 * 
	 * @param json
	 * @return
	 */
	private PMBean WeathercityAir(JSONObject json) {
		PMBean bean = null;
		try {
			int resultcode = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (resultcode == 200 && error_code == 0) {
				JSONObject resultJson = json.getJSONArray("result")
						.getJSONObject(0).getJSONObject("citynow");
				bean = new PMBean();
				bean.setAqi(resultJson.getString("AQI"));
				bean.setQuality(resultJson.getString("quality"));

			} else {
				Toast.makeText(getApplicationContext(), "PM2.5未获取到数据",
						Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bean;
	}

}
