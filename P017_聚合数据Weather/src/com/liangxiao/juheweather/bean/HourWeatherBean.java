package com.liangxiao.juheweather.bean;

public class HourWeatherBean {
	private String time;// 开始小时sh
	private String weather_id;
	private String temp;// 最低温度

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWeather_id() {
		return weather_id;
	}

	public void setWeather_id(String weather_id) {
		this.weather_id = weather_id;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

}
