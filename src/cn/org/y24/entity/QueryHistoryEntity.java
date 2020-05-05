package cn.org.y24.entity;

import cn.org.y24.interfaces.IEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueryHistoryEntity implements IEntity {
    private static final DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final CityEntity city;
    private final Date date;
    private final WeatherEntity weather;

    public QueryHistoryEntity(CityEntity city, Date date, WeatherEntity weather) {
        this.city = city;
        this.date = date;
        this.weather = weather;
    }

    public QueryHistoryEntity(String format) throws ParseException {
        final String[] strings = format.split("/", 3);
        city = new CityEntity(strings[0]);
        date = dataFormat.parse(strings[1]);
        weather = new WeatherEntity(strings[2]);
    }

    public String toString() {
        return city.toString() + "/" + dataFormat.format(date) + "/" + weather.toString();
    }


    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("city", city.toString());
        map.put("date", dataFormat.format(date));
        map.put("weather", weather.toString());
        return map;
    }
}
