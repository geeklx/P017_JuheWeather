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
	private static final int REPEAT_MSG = 0x01;// handler��ֵ����
	private static final int REFRESH_TIMES = 30 * 60 * 1000;

	/**
	 * �ص��ӿڲ���
	 * 
	 * @author lan
	 * 
	 */
	public interface OnParserCallBack {
		public void OnParserComplete(List<HourWeatherBean> hourlist,
				PMBean pmBean, WeatherBean weatherBean);
	}

	/**
	 * �ص����÷�������
	 * 
	 * @param onParserCallBack
	 */
	public void setCallBack(OnParserCallBack onParserCallBack) {
		this.onParserCallBack = onParserCallBack;
	}

	/**
	 * �ص��Ƴ���������
	 */
	public void removeCallBack() {
		onParserCallBack = null;
	}

	public void getCityWeather(String citys) {
		this.city = citys;
		getCityWeather();
	}

	/**
	 * ��activityͨ�Ų���
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
		city = "����";
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
				// �������ݲ���
				getCityWeather();
				// ����һ��message
				// handleMessage(msg);
				sendEmptyMessageDelayed(REPEAT_MSG, REFRESH_TIMES);
				// Toast.makeText(getApplicationContext(), "��ˢ���ˣ�", 2).show();

			}
		};
	};

	/**
	 * ��������ȡjson�󴫵ݸ��ص�������OnParserComplete����
	 */
	public void getCityWeather() {
		if (isRunning) {
			return;
		}
		isRunning = true;
		Log.v("count", count + "");
		count = 0;

		// ����Ԥ��
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
					// ���ÿؼ����ݲ���
					// setWeatherViews(weatherBean);
				}

				if (count == 3) {
					// mPullPullToRefreshScrollView.onRefreshComplete();
					if (onParserCallBack != null) {
						// ���ÿؼ����ݲ���
						onParserCallBack.OnParserComplete(hourlist, pmBean,
								weatherBean);
					}

					isRunning = false;
				}

			}
		});

		// ��Сʱ����
		data.getForecast3h(city, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				count++;
				hourlist = WeathergetForecast3h(arg0);
				if (hourlist != null && hourlist.size() >= 5) {
					// ���ÿؼ����ݲ���
					// setHourViews3h(list);
				}
				if (count == 3) {
					// mPullPullToRefreshScrollView.onRefreshComplete();
					if (onParserCallBack != null) {
						// ���ÿؼ����ݲ���
						onParserCallBack.OnParserComplete(hourlist, pmBean,
								weatherBean);
					}

					isRunning = false;
				}
			}
		});

		// PM2.5ֵ
		AirData pmData = AirData.getInstance();
		pmData.cityAir(city, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				count++;
				pmBean = WeathercityAir(arg0);
				if (pmBean != null) {
					// ���ÿؼ����ݲ���
					// setPMViews(bean);
				}
				if (count == 3) {
					// mPullPullToRefreshScrollView.onRefreshComplete();
					if (onParserCallBack != null) {
						// ���ÿؼ����ݲ���
						onParserCallBack.OnParserComplete(hourlist, pmBean,
								weatherBean);
					}

					isRunning = false;
				}
			}
		});
	}

	/**
	 * ��������Ԥ������
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
				JSONObject todayJson = resultJson.getJSONObject("today");// ��������
				bean.setCity(todayJson.getString("city"));// ����
				bean.setUv_index(todayJson.getString("uv_index"));// ������ǿ��
				bean.setTemp(todayJson.getString("temperature"));// �����¶�
				bean.setWeather_str(todayJson.getString("weather"));// ��������
				bean.setWeather_id(todayJson.getJSONObject("weather_id")// ����Ψһ��ʶ
						.getString("fa"));// ������ʶ00����
				bean.setDressing_index(todayJson.getString("dressing_index"));// ����ָ�������䣩

				// sk
				JSONObject skJson = resultJson.getJSONObject("sk");// ��ǰʵ������
				bean.setWind(skJson.getString("wind_direction")// ��ǰ����
						+ skJson.getString("wind_strength"));// ��ǰ����
				bean.setNow_temp(skJson.getString("temp"));// ��ǰ�¶�
				bean.setRelase(skJson.getString("time") + "����");// ����ʱ��
				bean.setHumidity(skJson.getString("humidity"));// ��ǰʪ��
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
						// ��ȡ��ʱ�����ڵ�ǰʱ�䲻ȡֵ����
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
				Toast.makeText(getApplicationContext(), "����Ԥ��δ��ȡ������",
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
	 * ������Сʱ��������Ԥ������
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

				// ������ݵ�����
				list = new ArrayList<HourWeatherBean>();
				// �������JSONArray
				JSONArray resultArray = json.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					// ������е�i������
					JSONObject resultObject = resultArray.getJSONObject(i);
					// ʵ�����������
					HourWeatherBean HBean = new HourWeatherBean();
					Date dateh = sdf.parse(resultObject.getString("sfdate"));
					if (!dateh.after(date)) {
						// ��ȡ��ʱ�����ڵ�ǰʱ�䲻ȡֵ����
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
				Toast.makeText(getApplicationContext(), "��Сʱ��������δ��ȡ������",
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
	 * ����PM2.5����
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
				Toast.makeText(getApplicationContext(), "PM2.5δ��ȡ������",
						Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bean;
	}

}
