package cn.org.y24.test;

import cn.org.y24.actions.CityAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.CityActionType;
import cn.org.y24.manager.CityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityManagerTest {

    @Test
    void execute() {
        CityManager manager = new CityManager();
        CityAction action = new CityAction(CityActionType.fetch);
        assertTrue(manager.execute(action));
        System.out.println(action.getCityList().size());
    }
}