package cn.org.y24.test;

import cn.org.y24.actions.CityWeatherAction;
import cn.org.y24.actions.HistoryAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.CityEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.enums.CityWeatherActionType;
import cn.org.y24.enums.HistoryActionType;
import cn.org.y24.manager.CityWeatherManager;
import cn.org.y24.manager.QueryHistoryManager;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class QueryHistoryManagerTest {

    @Test
    void execute() {
        final var cityWeatherManager = new CityWeatherManager();
        final var historyManager = new QueryHistoryManager();
        final var account = new AccountEntity("y24", "yue");
        List<CityEntity> cities = new ArrayList<>();
        cities.add(new CityEntity("jiangxi", "fuzhou"));
        cities.add(new CityEntity("jiangxi", "nanchang"));
        cities.add(new CityEntity("sichuan", "chengdu"));
        final List<QueryHistoryEntity> data = new ArrayList<>();
        cities.forEach(cityEntity -> {
            final var action = new CityWeatherAction(CityWeatherActionType.fetch, cityEntity);
            if (cityWeatherManager.execute(action)) {
                data.add(new QueryHistoryEntity(cityEntity, new Date(), action.getWeather()));
            }
        });
        final HistoryAction pushAction = new HistoryAction(HistoryActionType.push, account, data);
        assertTrue(historyManager.execute(pushAction));
        final HistoryAction fetchAction = new HistoryAction(HistoryActionType.fetch, account);
        assertTrue(historyManager.execute(fetchAction));
        final var res = fetchAction.getHistoryList();
        System.out.println(res);
    }
}