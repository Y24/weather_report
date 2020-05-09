package cn.org.y24.actions;

import cn.org.y24.entity.CityEntity;
import cn.org.y24.entity.WeatherEntity;
import cn.org.y24.enums.CityWeatherActionType;

import java.util.List;

public class CityWeatherAction {
    private final CityWeatherActionType type;
    private final CityEntity cityEntity;
    private List<WeatherEntity> weather;

    public CityEntity getCityEntity() {
        return cityEntity;
    }

    public CityWeatherActionType getType() {
        return type;
    }

    public List<WeatherEntity> getWeather() {
        return weather;
    }

    public void setWeather( List<WeatherEntity> weather) {
        this.weather = weather;
    }

    public CityWeatherAction(CityWeatherActionType type, CityEntity cityEntity) {
        this.type = type;
        this.cityEntity = cityEntity;
    }
}
