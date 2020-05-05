package cn.org.y24;

import cn.org.y24.actions.CityWeatherAction;
import cn.org.y24.entity.CityEntity;
import cn.org.y24.enums.CityWeatherActionType;
import cn.org.y24.manager.CityWeatherManager;

public class Main {

    public static void main(String[] args) {
        final var manager = new CityWeatherManager();
        final CityWeatherAction action = new CityWeatherAction(CityWeatherActionType.fetch, new CityEntity("jiangxi", "fuzhou"));
        if (manager.execute(action)) {
            System.out.println("Good");
            System.out.println(action.getWeather().toString());
        } else {
            System.out.println("Bad");
        }
    }
}
