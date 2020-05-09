package cn.org.y24.entity;

import cn.org.y24.interfaces.IEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class Weather {
    private final StringProperty date;
    private final StringProperty temperature;
    private final StringProperty weather;
    private final StringProperty lastUpdateTime;

    public Weather() {
        date = new SimpleStringProperty(this, "date");
        temperature = new SimpleStringProperty(this, "temp");
        weather = new SimpleStringProperty(this, "weather");
        lastUpdateTime = new SimpleStringProperty(this, "updateTime");
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getTemperature() {
        return temperature.get();
    }

    public StringProperty temperatureProperty() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature.set(temperature);
    }

    public String getWeather() {
        return weather.get();
    }

    public StringProperty weatherProperty() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather.set(weather);
    }

    public String getLastUpdateTime() {
        return lastUpdateTime.get();
    }

    public StringProperty lastUpdateTimeProperty() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime.set(lastUpdateTime);
    }
}
