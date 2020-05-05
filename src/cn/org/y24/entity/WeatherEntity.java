package cn.org.y24.entity;

import cn.org.y24.interfaces.IEntity;

import java.util.HashMap;
import java.util.Map;

public class WeatherEntity implements IEntity {
    public final static WeatherEntity nullWeatherEntity = new WeatherEntity("", 0, "", "", "", "", "");
    private final String alerts;
    private final int temperature;
    private final String weather;
    private final String lastUpdateTime;
    /// 湿度
    private final String humidity;
    private final String windInfo;
    private final String tips;

    public boolean isNull() {
        return lastUpdateTime.equals("");
    }

    public WeatherEntity(String target) {
        assert (!target.equals(""));
        final String[] strings = target.split("/", 8);
        this.alerts = strings[0];
        this.temperature = Integer.parseInt(strings[1]);
        this.weather = strings[2];
        this.lastUpdateTime = strings[3];
        this.humidity = strings[4];
        this.windInfo = strings[5];
        this.tips = strings[6];
    }

    public WeatherEntity(String alerts, int temperature, String humidity, String weather, String windInfo, String lastUpdateTime, String tips) {
        this.alerts = alerts;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weather = weather;
        this.windInfo = windInfo;
        this.lastUpdateTime = lastUpdateTime;
        this.tips = tips;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWeather() {
        return weather;
    }

    public String getWindInfo() {
        return windInfo;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getTips() {
        return tips;
    }


    public String toString() {
        return alerts +
                "/" + temperature +
                "/" + weather +
                "/" + lastUpdateTime +
                "/" + humidity +
                "/" + windInfo +
                "/" + tips;
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("alerts", alerts);
        map.put("temperature", temperature + "");
        map.put("humidity", humidity);
        map.put("weather", weather);
        map.put("windInfo", windInfo);
        map.put("tips", tips);
        map.put("lastUpdateTime", lastUpdateTime);
        return map;
    }
}
