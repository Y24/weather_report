package cn.org.y24.actions;

import cn.org.y24.entity.CityEntity;
import cn.org.y24.entity.WeatherEntity;
import cn.org.y24.enums.CityWeatherActionType;

public class CityWeatherAction {
    private final CityWeatherActionType type;
    private final CityEntity cityEntity;
    WeatherEntity weather;

    public CityEntity getCityEntity() {
        return cityEntity;
    }

    public CityWeatherActionType getType() {
        return type;
    }

    public WeatherEntity getWeather() {
        return weather;
    }

    public void setWeather(WeatherEntity weather) {
        this.weather = weather;
    }

    public CityWeatherAction(CityWeatherActionType type, CityEntity cityEntity) {
        this.type = type;
        this.cityEntity = cityEntity;
    }
}
