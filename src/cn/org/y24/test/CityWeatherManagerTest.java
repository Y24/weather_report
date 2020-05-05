package cn.org.y24.test;

import cn.org.y24.actions.CityWeatherAction;
import cn.org.y24.manager.CityWeatherManager;
import cn.org.y24.entity.CityEntity;
import cn.org.y24.enums.CityWeatherActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CityWeatherManagerTest {

    @DisplayName("Test fetch function of CityWeatherManager")
    @Test
    void fetch() {
        final var manager = new CityWeatherManager();
        final Map<CityEntity, Boolean> ans = new HashMap<>();
        ans.put(new CityEntity("jiangxi", "fuzhou"), true);
        ans.put(new CityEntity("jiangxi", "fuzho"), false);
        ans.forEach((city, result) -> {
            final CityWeatherAction action = new CityWeatherAction(CityWeatherActionType.fetch, city);
            assertEquals(result, manager.execute(action));
        });

    }
}