package com.liangxiao.juheweather.bean;

import java.util.List;

public class WeatherBean {

	private String city;// 城市city
	private String relase;// 更新时间time
	private String weather_id;// 天气ID weather_id
	private String weather_str;// 天气文字。如雾weather
	private String temp;// 当天的温度temperature
	private String now_temp;// 当时的温度temp
	private String aqi;// 空气质量指数
	private String quality;// 空气质量文字
	private String felt_temp;// 体感温度temp
	private String humidity;// 当前湿度humidity
	private String wind;// 风向与风力wind
	private String uv_index;// 紫外线强度uv_index
	private String dressing_index;// 穿衣指数dressing_index

	private List<FutureWeatherBean> futureList;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRelase() {
		return relase;
	}

	public void setRelase(String relase) {
		this.relase = relase;
	}

	public String getWeather_id() {
		return weather_id;
	}

	public void setWeather_id(String weather_id) {
		this.weather_id = weather_id;
	}

	public String getWeather_str() {
		return weather_str;
	}

	public void setWeather_str(String weather_str) {
		this.weather_str = weather_str;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getNow_temp() {
		return now_temp;
	}

	public void setNow_temp(String now_temp) {
		this.now_temp = now_temp;
	}

	public String getAqi() {
		return aqi;
	}

	public void setAqi(String aqi) {
		this.aqi = aqi;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getFelt_temp() {
		return felt_temp;
	}

	public void setFelt_temp(String felt_temp) {
		this.felt_temp = felt_temp;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getUv_index() {
		return uv_index;
	}

	public void setUv_index(String uv_index) {
		this.uv_index = uv_index;
	}

	public String getDressing_index() {
		return dressing_index;
	}

	public void setDressing_index(String dressing_index) {
		this.dressing_index = dressing_index;
	}

	public List<FutureWeatherBean> getFutureList() {
		return futureList;
	}

	public void setFutureList(List<FutureWeatherBean> futureList) {
		this.futureList = futureList;
	}

}
