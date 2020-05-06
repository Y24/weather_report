package cn.org.y24.test;

import cn.org.y24.entity.CityEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityEntityTest {

    @Test
    void testEquals() {
        final CityEntity city0 = new CityEntity("yue", "cui", "love you");
        final CityEntity city1 = new CityEntity("yue", "cui");
        assertEquals(city0, city1);
        assertNotSame(city0, city1);
    }
}